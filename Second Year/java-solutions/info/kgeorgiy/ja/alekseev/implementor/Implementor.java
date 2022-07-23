package info.kgeorgiy.ja.alekseev.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

/**
 * Class/interface implementor.
 *
 * @author Ivan Alexeev
 */

public class Implementor implements JarImpler {
    /**
     * Produces code implementing class or interface specified by provided {@code token}.
     * <p>
     * Generated class classes name should be same as classes name of the type token with {@code Impl} suffix
     * added. Generated source code should be placed in the correct subdirectory of the specified
     * {@code root} directory and have correct file name.
     *
     *
     * @param token type token to create implementation for.
     * @param root root directory.
     * @throws ImplerException when implementation cannot be generated.
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        if (tokenIsValid(token)) {
            final Path path = generatePath(token, root);

            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writeClass(writer, token);
            } catch (IOException e) {
                throw new ImplerException("Output error", e);
            }
        } else {
            throw new ImplerException("Invalid token: " +
                    (token == null ? CodeToken.NULL : token.getSimpleName())
            );
        }
    }

    /**
     * Produces <var>.jar</var> file implementing class or interface specified by provided <var>token</var>.
     * <p>
     * Generated class classes name should be same as classes name of the type token with <var>Impl</var> suffix
     * added.
     *
     * @param token type token to create implementation for.
     * @param jarFile target <var>.jar</var> file.
     * @throws ImplerException when implementation cannot be generated.
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        try {
            Files.createDirectories(jarFile.getParent());
            Path tmpDir = Files.createTempDirectory(jarFile.toAbsolutePath().getParent(), "tmpdir");

            try {
                implement(token, tmpDir);
                compileFiles(token, tmpDir);
                generateJar(token, tmpDir, jarFile);
            } finally {
                Files.walkFileTree(tmpDir, DELETE_VISITOR);
            }
        } catch (IOException e) {
            throw new ImplerException("IO exception", e);
        }
    }

    /**
     * Compiles implemented class specified by <var>token</var>.
     *
     * @param token type token to compile.
     * @param dirPath where file located.
     * @throws ImplerException when unable to find system compiler.
     * @throws ImplerException upon compilation error.
     * @throws ImplerException when cannot convert path to URI.
     */
    public static void compileFiles(Class<?> token, Path dirPath) throws ImplerException {
        try {
            Path classPath = Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path filePath = dirPath.resolve(generateClassPath(token));

            final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                throw new ImplerException("No java compiler");
            }

            final String[] args = {
                    filePath.toString(),
                    "-cp",
                    dirPath + CodeToken.PATH_SEPARATOR + classPath
            };

            int returnCode = compiler.run(null, null, null, args);
            if (returnCode != 0) {
                throw new ImplerException("Compilation Error");
            }
        } catch (URISyntaxException e) {
            throw new ImplerException("URI Error", e);
        }
    }

    /**
     * Generates <var>.jar</var> file of implemented class or interface.
     *
     * @param token type token to create implementation for.
     * @param dirPath where file located.
     * @param jarPath target <var>.jar</var> file.
     * @throws ImplerException upon {@link JarOutputStream} error.
     */
    private void generateJar(Class<?> token, Path dirPath, Path jarPath) throws ImplerException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

        String compiledFilePath = token.getPackageName()
                .replace('.', CodeToken.JAR_SEPARATOR)
                + CodeToken.JAR_SEPARATOR
                + generateImplName(token)
                + CodeToken.DOT_CLASS;

        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jarPath), manifest)) {
            output.putNextEntry(new ZipEntry(compiledFilePath));
            Files.copy(Paths.get(dirPath.toString(), compiledFilePath), output);
        } catch (IOException e) {
            throw new ImplerException("JarOutput Error", e);
        }
    }

    /**
     * Generates {@link Path} for provided root and token.
     *
     * @param token type token to create implementation for.
     * @param root from where create directories.
     * @throws ImplerException when file's parent directories cannot be generated.
     * @return generated path for token from root.
     */
    private Path generatePath(Class<?> token, Path root) throws ImplerException {
        Path parents = root.resolve(generateClassPath(token));
        if (parents.getParent() != null) {
            try {
                Files.createDirectories(parents.getParent());
            } catch (IOException e) {
                throw new ImplerException("Error with creation of parenting directories", e);
            }
        }
        return parents;
    }

    /**
     * Generates path for provided by token compiled class.
     *
     * @param token type token to create implementation for.
     * @return path for compiled class.
     */
    private static Path generateClassPath(Class<?> token) {
        return Path.of(token.getPackageName().replace('.', CodeToken.FILE_SEPARATOR))
                .resolve(generateImplName(token) + CodeToken.DOT_JAVA);
    }

    /**
     * Validate given token before implementation.
     * <p>
     *
     * Invalid tokens: Primitive, Array, Enum, final, private, null
     *
     * @param token type token to create implementation for.
     * @return {@code true} if token is valid.
     */
    private boolean tokenIsValid(Class<?> token) {
        return !(token == null ||
                token.isPrimitive() ||
                token.isArray() ||
                token.isEnum() ||
                token.equals(Enum.class) ||
                Modifier.isPrivate(token.getModifiers()) ||
                Modifier.isFinal(token.getModifiers())
        );
    }

    /**
     * Writes in file generated implementing class or interface specified by provided <var>token</var>.
     *
     * @param token type token to create implementation for.
     * @param writer writes to generated file.
     * @throws ImplerException when valid implementation cannot be generated.
     * @throws IOException when encounters exception during writing.
     */
    public static void writeClass(BufferedWriter writer, Class<?> token) throws IOException, ImplerException {
        writeClassPackage(writer, token);
        writeClassDeclaration(writer, token);
        writeClassConstructors(writer, token);
        writeClassMethods(writer, token);
    }

    /**
     * Writes class package code for newly generated file.
     *
     * @param token type token to create implementation for.
     * @param writer writes to generated file.
     * @throws IOException when encounters exception during writing.
     */
    private static void writeClassPackage(BufferedWriter writer, Class<?> token) throws IOException {
        String packageString = CodeToken.EMPTY;
        Package p = token.getPackage();

        if (p != null) {
            packageString = CodeToken.PACKAGE + CodeToken.SPACE + p.getName()
                    + CodeToken.SEMICOLON + CodeToken.NEW_LINE;
        }

        write(writer, packageString);
    }

    /**
     * Writes class declaration code for newly generated file.
     *
     * @param token type token to create implementation for.
     * @param writer writes to generated file.
     * @throws IOException when encounters exception during writing.
     */
    private static void writeClassDeclaration(BufferedWriter writer, Class<?> token) throws IOException {
        String declarationString = CodeToken.SPACE + CodeToken.PUBLIC + CodeToken.SPACE +
                CodeToken.CLASS + CodeToken.SPACE + generateImplName(token) + CodeToken.SPACE +
                (token.isInterface() ? CodeToken.IMPLEMENTS : CodeToken.EXTENDS) +
                CodeToken.SPACE + token.getCanonicalName() + CodeToken.SPACE +
                CodeToken.CURLY_OPEN_BRACKET + CodeToken.NEW_LINE;

        write(writer, declarationString);
    }

    /**
     * Writes class constructors for newly generated file.
     * <p>
     * If <var>token</var> is interface then it doesn't write anything.
     *
     * @param token type token to create implementation for.
     * @param writer writes to generated file.
     * @throws IOException when encounters exception during writing.
     * @throws ImplerException when no valid constructors are found.
     */
    private static void writeClassConstructors(BufferedWriter writer, Class<?> token) throws ImplerException, IOException {
        if (!token.isInterface()) {
            Constructor<?>[] constructors = Arrays.stream(token.getDeclaredConstructors())
                    .filter(c -> !Modifier.isPrivate(c.getModifiers()))
                    .toArray(Constructor<?>[]::new);

            if (constructors.length > 0) {
                for (Constructor<?> c : constructors) {
                    write(writer, generateConstructor(c));
                }
            } else {
                throw new ImplerException("No constructors in class");
            }
        }
    }

    /**
     * Writes not realised class methods for newly generated file.
     *
     * @param token type token to create implementation for.
     * @param writer writes to generated file.
     * @throws IOException when encounters exception during writing.
     */
    private static void writeClassMethods(BufferedWriter writer, Class<?> token) throws IOException {
        Set<Method> extendMethods = new TreeSet<>(METHOD_COMPARATOR);
        Set<Method> allMethods = new TreeSet<>(METHOD_COMPARATOR);

        getMethods(token.getMethods(), extendMethods, allMethods);

        for (Class<?> clazz = token; clazz != null; clazz = clazz.getSuperclass()) {
            allMethods.addAll(
                    Arrays.stream(clazz.getDeclaredMethods())
                            .filter(m -> !Modifier.isAbstract(m.getModifiers()))
                            .toList()
            );
            getMethods(clazz.getDeclaredMethods(), extendMethods, allMethods);
        }

        for (Method m : extendMethods) {
            write(writer, generateMethod(m));
        }
        write(writer, CodeToken.CURLY_CLOSE_BRACKET + CodeToken.NEW_LINE);
    }

    /**
     * Private function for current level of parenting tree.
     *
     * @param methods declared methods of the class.
     * @param extendMethods methods to extend.
     * @param allMethods all methods collected during walk.
     */
    private static void getMethods(Method[] methods, Set<Method> extendMethods, Set<Method> allMethods) {
        for (Method method : methods) {
            if (!allMethods.contains(method)) {
                if (Modifier.isAbstract(method.getModifiers())) extendMethods.add(method);
                else allMethods.add(method);
            }
        }
    }

    /**
     * Returns string containing generated method.
     *
     * @param method given method.
     * @return {@link String} of generated <var>method</var>.
     */
    private static String generateMethod(Method method) {
        String prefixString = method.getReturnType().getCanonicalName() + CodeToken.SPACE + method.getName();
        String bodyString = CodeToken.TAB.repeat(2) + CodeToken.RETURN + CodeToken.SPACE +
                generateDefaultReturnValue(method.getReturnType()) + CodeToken.SEMICOLON;
        return generateExecutable(method, prefixString, bodyString);
    }

    /**
     * Returns string containing generated constructor.
     *
     * @param constructor given constructor.
     * @return {@link String} of generated <var>constructor</var>.
     */
    private static String generateConstructor(Constructor<?> constructor) {
        String bodyString = CodeToken.TAB.repeat(2) + CodeToken.SUPER + CodeToken.OPEN_BRACKET +
                generateParameters(constructor, false) + CodeToken.CLOSE_BRACKET + CodeToken.SEMICOLON;
        return generateExecutable(constructor, generateImplName(constructor.getDeclaringClass()), bodyString);
    }

    /**
     * Generates default return value for <var>returnType</var>
     *
     * @param returnType tokenType for returning value.
     * @return {@link String} of generated default value.
     */
    private static String generateDefaultReturnValue(Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            return CodeToken.NULL;
        } else if (returnType.equals(void.class)) {
            return CodeToken.EMPTY;
        } else if (returnType.equals(boolean.class)) {
            return CodeToken.FALSE;
        }
        return CodeToken.ZERO;
    }

    /**
     * Returns string containing generated executable.
     *
     * @param executable given executable.
     * @param executablePrefix given executable prefix.
     * @param executableBody given executable body.
     * @return {@link String} of generated executable.
     */
    private static String generateExecutable(Executable executable,
                                             String executablePrefix,
                                             String executableBody) {
        return CodeToken.NEW_LINE + CodeToken.TAB + CodeToken.PUBLIC + CodeToken.SPACE +
                executablePrefix + CodeToken.OPEN_BRACKET + generateParameters(executable, true) +
                CodeToken.CLOSE_BRACKET + generateExceptions(executable) + CodeToken.SPACE +
                CodeToken.CURLY_OPEN_BRACKET + CodeToken.NEW_LINE + executableBody +
                CodeToken.NEW_LINE + CodeToken.TAB + CodeToken.CURLY_CLOSE_BRACKET +
                CodeToken.NEW_LINE;
    }

    /**
     * Returns string containing generated parameters for executable.
     *
     * @param executable given executable.
     * @param withTypes return parameters with types.
     * @return {@link String} of generated parameters.
     */
    private static String generateParameters(Executable executable, boolean withTypes) {
        return Arrays.stream(executable.getParameters())
                .map(p -> (
                                (withTypes
                                        ? p.getType().getCanonicalName() + CodeToken.SPACE + p.getName()
                                        : p.getName()
                                )
                        )
                )
                .collect(Collectors.joining(CodeToken.COMMA));
    }

    /**
     * Returns string containing generated exceptions for executable.
     *
     * @param executable given executable.
     * @return {@link String} of generated exceptions.
     */
    private static String generateExceptions(Executable executable) {
        Class<?>[] exceptionTypes = executable.getExceptionTypes();
        if (exceptionTypes.length == 0) {
            return CodeToken.EMPTY;
        }

        return CodeToken.SPACE + CodeToken.THROWS +
                CodeToken.SPACE +
                Arrays.stream(exceptionTypes)
                        .map(Class::getCanonicalName)
                        .collect(Collectors.joining(CodeToken.COMMA));
    }

    /**
     * Writes in valid format.
     *
     * @param writer writes to generated file.
     * @param code given code to write.
     * @throws IOException when encounters exception during writing.
     */
    private static void write(BufferedWriter writer, String code) throws IOException {
        for (char symbol : code.toCharArray()) {
            if (symbol < 128) writer.write(symbol);
            else writer.write("\\u" + String.format("%04X", (int) symbol));
        }
    }

    /**
     * Generates class name with Impl suffix.
     *
     * @param token for generation.
     * @return generated Impl name.
     */
    public static String generateImplName(Class<?> token) {
        return token.getSimpleName() + CodeToken.IMPL;
    }

    /**
     * Main function of class.
     * <p>
     * If provided with two arguments, will use them in implement(token, path).
     * <p>
     * If provided with three arguments, first should be {@code -jar }
     * and will use them in implementJar(token, path).
     * <p>
     * Arguments should be valid and not null.
     *
     * @param args for command arguments.
     */
    public static void main(String[] args) {
        if (args == null) {
            System.err.println("No arguments given");
            return;
        } else if (args.length != 2 && args.length != 3) {
            System.err.println("Wrong number of arguments");
            return;
        }

        for (String s : args) {
            if (s == null) {
                System.err.println("Invalid argument: null");
                return;
            }
        }

        try {
            Class<?> clazz;
            Path path;

            if (args.length == 2) {
                clazz = Class.forName(args[0]);
                path = Path.of(args[1]);
            } else {
                if (!args[0].equals("-jar")) {
                    System.err.println("Invalid command: need -jar for three arguments");
                    return;
                }
                clazz = Class.forName(args[1]);
                path = Path.of(args[2]);
            }

            Implementor implementor = new Implementor();
            implementor.implement(clazz, path);
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid argument: class");
        } catch (InvalidPathException e) {
            System.err.println("Invalid argument: path");
        } catch (ImplerException e) {
            System.err.println("Implementing error: " + e.getLocalizedMessage());
        }
    }

    /**
     * Private {@link SimpleFileVisitor} for cleaning temporary directories.
     */
    private static final SimpleFileVisitor<Path> DELETE_VISITOR = new SimpleFileVisitor<>() {
        /**
         * Delete file upon visit, and then continue walking.
         *
         * @param file to visit.
         * @param attrs file attributes.
         * @throws IOException if unable to delete file.
         * @return {#FileVisitResult.CONTINUE}
         */
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        /**
         * Delete directory upon visit, and then continue walking.
         *
         * @param dir to visit.
         * @param exc exception during visit.
         * @throws IOException if unable to delete directory.
         * @return {#FileVisitResult.CONTINUE}
         */
        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    };

    /**
     * Method comparator to get not realised methods.
     */
    private static final Comparator<Method> METHOD_COMPARATOR =
            Comparator.comparing(Method::getName)
                    .thenComparing(Method::isVarArgs)
                    .thenComparing((o1, o2) -> Arrays.compare(
                                    o1.getParameterTypes(),
                                    o2.getParameterTypes(),
                                    Comparator.comparing(Class::getName)
                            )
                    );

    /**
     * Utility class for various code tokens, useful in file generating.
     */
    public static final class CodeToken {
        /**
         * Suffix for implemented files.
         */
        public static final String IMPL = "Impl";

        /**
         * Java file extension.
         */
        public static final String DOT_JAVA = ".java";

        /**
         * Compiled java file extension.
         */
        public static final String DOT_CLASS = ".class";

        /**
         * Space char for generated code.
         */
        public static final String SPACE = " ";

        /**
         * Empty string for generated code.
         */
        public static final String EMPTY = "";

        /**
         * Comma separator for generated code.
         */
        public static final String COMMA = ", ";

        /**
         * Semicolon for generated code.
         */
        public static final String SEMICOLON = ";";

        /**
         * Open bracket for generated code.
         */
        public static final String OPEN_BRACKET = "(";

        /**
         * Close bracket for generated code.
         */
        public static final String CLOSE_BRACKET = ")";

        /**
         * Curly open bracket for generated code.
         */
        public static final String CURLY_OPEN_BRACKET = "{";

        /**
         * Curly close bracket for generated code.
         */
        public static final String CURLY_CLOSE_BRACKET = "}";

        /**
         * New line character for generated code.
         */
        public static final String NEW_LINE = System.lineSeparator();

        /**
         * Path separator for generated path.
         */
        public static final String PATH_SEPARATOR = File.pathSeparator;

        /**
         * File separator for generated path.
         */
        public static final char FILE_SEPARATOR = File.separatorChar;

        /**
         * File separator for generated jar.
         */
        public static final char JAR_SEPARATOR = '/';

        /**
         * Tab character for generated code.
         */
        public static final String TAB = "\t";

        /**
         * Special word 'public' for generated code.
         */
        public static final String PUBLIC = "public";

        /**
         * Special word 'class' for generated code.
         */
        public static final String CLASS = "class";

        /**
         * Special word 'extends' for generated code.
         */
        public static final String EXTENDS = "extends";

        /**
         * Special word 'implements' for generated code.
         */
        public static final String IMPLEMENTS = "implements";

        /**
         * Special word 'package' for generated code.
         */
        public static final String PACKAGE = "package";

        /**
         * Special word 'return' for generated code.
         */
        public static final String RETURN = "return";

        /**
         * Special word 'throws' for generated code.
         */
        public static final String THROWS = "throws";

        /**
         * Special word 'super' for generated code.
         */
        public static final String SUPER = "super";

        /**
         * Special word 'null' for generated code.
         */
        public static final String NULL = "null";

        /**
         * Special word 'false' for generated code.
         */
        public static final String FALSE = "false";

        /**
         * Zero character for generated code.
         */
        public static final String ZERO = "0";
    }
}
