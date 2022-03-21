package info.kgeorgiy.ja.alekseev.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Implementor implements Impler {
    /**
     * Produces code implementing class or interface specified by provided {@code token}.
     * <p>
     * Generated class classes name should be same as classes name of the type token with {@code Impl} suffix
     * added. Generated source code should be placed in the correct subdirectory of the specified
     * {@code root} directory and have correct file name. For example, the implementation of the
     * interface {@link java.util.List} should go to {@code $root/java/util/ListImpl.java}
     *
     *
     * @param token type token to create implementation for.
     * @param root root directory.
     * @throws info.kgeorgiy.java.advanced.implementor.ImplerException when implementation cannot be
     * generated.
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        if (tokenIsValid(token)) {
            final Path path = generatePath(token, root);

            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writeClassPackage(writer, token);
                writeClassDeclaration(writer, token);
                writeClassConstructors(writer, token);
                writeClassMethods(writer, token);
            } catch (IOException e) {
                throw new ImplerException("Cannot create output file");
            }
        } else {
            throw new ImplerException("Invalid token");
        }
    }

    private void writeClassPackage(BufferedWriter writer, Class<?> token) throws IOException {
        StringBuilder packageBuilder = new StringBuilder(CodeToken.NEW_LINE);
        Package p = token.getPackage();

        if (p != null) {
            packageBuilder.append(CodeToken.SPACE)
                    .append(CodeToken.PACKAGE)
                    .append(CodeToken.SPACE)
                    .append(p.getName())
                    .append(CodeToken.SEMICOLON)
                    .append(CodeToken.NEW_LINE);
        }

        writer.write(packageBuilder.toString());
    }

    private void writeClassDeclaration(BufferedWriter writer, Class<?> token) throws IOException {
        StringBuilder declarationBuilder = new StringBuilder(CodeToken.SPACE);

        declarationBuilder.append(CodeToken.PUBLIC)
                .append(CodeToken.SPACE)
                .append(CodeToken.CLASS)
                .append(CodeToken.SPACE)
                .append(generateImplName(token))
                .append(CodeToken.SPACE)
                .append(token.isInterface() ? CodeToken.IMPLEMENTS : CodeToken.EXTENDS)
                .append(CodeToken.SPACE)
                .append(token.getCanonicalName())
                .append(CodeToken.SPACE)
                .append(CodeToken.CURLY_OPEN_BRACKET)
                .append(CodeToken.NEW_LINE);

        writer.write(declarationBuilder.toString());
    }


    private void writeClassConstructors(BufferedWriter writer, Class<?> token) throws ImplerException, IOException {
        if (!token.isInterface()) {
            Constructor<?>[] constructors = Arrays.stream(token.getDeclaredConstructors())
                    .filter(c -> !Modifier.isPrivate(c.getModifiers()))
                    .toArray(Constructor<?>[]::new);

            if (constructors.length > 0) {
                for (Constructor<?> c : constructors) {
                    writer.write(generateConstructor(c));
                }
            } else {
                throw new ImplerException("No constructors in class");
            }
        }
    }

    private void writeClassMethods(BufferedWriter writer, Class<?> token) throws IOException {
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
            writer.write(generateMethod(m));
        }
        writer.write(CodeToken.CURLY_CLOSE_BRACKET + CodeToken.NEW_LINE);
    }

    private void getMethods (Method[] methods, Set<Method> extendMethods, Set<Method> allMethods) {
        for (Method method : methods) {
            if (!allMethods.contains(method)) {
                if (Modifier.isAbstract(method.getModifiers())) extendMethods.add(method);
                else allMethods.add(method);
            }
        }
    }

    private final Comparator<Method> METHOD_COMPARATOR =
            Comparator.comparing(Method::getName)
            .thenComparing(Method::isVarArgs)
            .thenComparing((o1, o2) -> Arrays.compare(
                    o1.getParameterTypes(),
                    o2.getParameterTypes(),
                    Comparator.comparing(Class::getName)
                    )
            );

    private String generateMethod(Method m) {
        StringBuilder prefixBuilder = new StringBuilder(m.getReturnType().getCanonicalName());
        prefixBuilder.append(CodeToken.SPACE).append(m.getName());

        StringBuilder bodyBuilder = new StringBuilder(CodeToken.TAB.repeat(2));
        bodyBuilder.append(CodeToken.RETURN)
                .append(CodeToken.SPACE)
                .append(generateDefaultReturnValue(m.getReturnType()))
                .append(CodeToken.SEMICOLON);

        return generateExecutable(m, prefixBuilder.toString(), bodyBuilder.toString());
    }

    private String generateConstructor(Constructor<?> c) {
        StringBuilder prefixBuilder = new StringBuilder(generateImplName(c.getDeclaringClass()));

        StringBuilder bodyBuilder = new StringBuilder(CodeToken.TAB.repeat(2));
        bodyBuilder.append(CodeToken.SUPER)
                .append(CodeToken.OPEN_BRACKET)
                .append(generateParameters(c, false))
                .append(CodeToken.CLOSE_BRACKET)
                .append(CodeToken.SEMICOLON);

        return generateExecutable(c, prefixBuilder.toString(), bodyBuilder.toString());
    }

    private String generateDefaultReturnValue(Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            return CodeToken.NULL;
        } else if (returnType.equals(void.class)) {
            return CodeToken.EMPTY;
        } else if (returnType.equals(boolean.class)) {
            return CodeToken.TRUE;
        }
        return CodeToken.ZERO;
    }

    private String generateExecutable(Executable executable,
                                      String executablePrefix,
                                      String executableBody) {
        StringBuilder executableBuilder = new StringBuilder();

        executableBuilder.append(CodeToken.NEW_LINE)
                .append(CodeToken.TAB)
                .append(CodeToken.PUBLIC)
                .append(CodeToken.SPACE)
                .append(executablePrefix)
                .append(CodeToken.OPEN_BRACKET)
                .append(generateParameters(executable, true))
                .append(CodeToken.CLOSE_BRACKET)
                .append(generateExceptions(executable))
                .append(CodeToken.SPACE)
                .append(CodeToken.CURLY_OPEN_BRACKET)
                .append(CodeToken.NEW_LINE)
                .append(executableBody)
                .append(CodeToken.NEW_LINE)
                .append(CodeToken.TAB)
                .append(CodeToken.CURLY_CLOSE_BRACKET)
                .append(CodeToken.NEW_LINE);

        return executableBuilder.toString();
    }

    private String generateParameters(Executable executable, boolean withTypes) {
        return Arrays.stream(executable.getParameters())
                .map(p -> (
                        (withTypes
                                ? p.getType().getCanonicalName() + CodeToken.SPACE
                                : CodeToken.EMPTY
                        ) + p.getName()
                        )
                )
                .collect(Collectors.joining(CodeToken.COMMA));
    }

    private String generateExceptions(Executable executable) {
        Class<?>[] exceptionTypes = executable.getExceptionTypes();
        if (exceptionTypes.length == 0) {
            return CodeToken.EMPTY;
        }

        StringBuilder exceptionsBuilder = new StringBuilder(CodeToken.SPACE);
        exceptionsBuilder.append(CodeToken.THROWS)
                .append(CodeToken.SPACE)
                .append(Arrays.stream(exceptionTypes)
                        .map(Class::getCanonicalName)
                        .collect(Collectors.joining(CodeToken.COMMA))
                );

        return exceptionsBuilder.toString();
    }

    private Path generatePath(Class<?> token, Path root) throws ImplerException {
        Path parents = root
                .resolve(token.getPackageName().replace('.', File.separatorChar))
                .resolve(generateImplName(token) + CodeToken.JAVA);

        if (parents.getParent() != null) {
            try {
                Files.createDirectories(parents.getParent());
            } catch (IOException e) {
                throw new ImplerException("Exception with creating parent dirs.");
            }
        }

        return parents;
    }

    private boolean tokenIsValid(Class<?> token) {
        return !(token.isPrimitive() ||
                token.isArray() ||
                token.isEnum() ||
                token.equals(Enum.class) ||
                Modifier.isPrivate(token.getModifiers()) ||
                Modifier.isFinal(token.getModifiers())
        );
    }

    private String generateImplName(Class<?> token) {
        return token.getSimpleName() + CodeToken.IMPL;
    }

    private static final class CodeToken {
        public static final String IMPL = "Impl";
        public static final String JAVA = ".java";

        public static final String SPACE = " ";
        public static final String EMPTY = "";
        public static final String COMMA = ", ";
        public static final String SEMICOLON = ";";
        public static final String OPEN_BRACKET = "(";
        public static final String CLOSE_BRACKET = ")";
        public static final String CURLY_OPEN_BRACKET = "{";
        public static final String CURLY_CLOSE_BRACKET = "}";
        public static final String NEW_LINE = System.lineSeparator();
        public static final char FILE_SEPARATOR = File.separatorChar;
        public static final String TAB = "\t";

        public static final String PUBLIC = "public";
        public static final String CLASS = "class";
        public static final String EXTENDS = "extends";
        public static final String IMPLEMENTS = "implements";
        public static final String PACKAGE = "package";
        public static final String RETURN = "return";
        public static final String THROWS = "throws";
        public static final String SUPER = "super";

        public static final String NULL = "null";
        public static final String TRUE = "true";
        public static final String ZERO = "0";
    }
}
