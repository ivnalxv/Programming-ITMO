package info.kgeorgiy.ja.alekseev.walk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class AbstractWalk {
    protected MessageDigest messageDigest;
    protected abstract RecursiveVisitor getVisitor(BufferedWriter writer, MessageDigest messageDigest);

    AbstractWalk() {
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Hashing algorithm not available");
        }
    }

    public void walk(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Usage: java " + this.getClass().getSimpleName() + " [input_file] [output_file]");
            return;
        }

        try {
            walk(args[0], args[1]);
        } catch (WalkException e) {
            System.err.println(e.getMessage());
        }
    }

    private static Path convertToPath(String filename) throws WalkException {
        try {
            return Path.of(filename);
        } catch (InvalidPathException e) {
            throw new WalkException("Invalid filename: " + filename, e);
        }
    }

    private void walk(String inputFilename, String outputFilename) throws WalkException {
        final Path inputPath = convertToPath(inputFilename);
        final Path outputPath = convertToPath(outputFilename);

        try (BufferedReader reader = Files.newBufferedReader(inputPath)) {
            try {
                Path outputParent = outputPath.getParent();
                if (outputParent != null) {
                    Files.createDirectories(outputParent);
                }
            } catch (IOException ignored) {
                // Exception should be ignored, because directory can already exist,
                // but we're not allowed to create a new one.
//                throw new WalkException("Unable to create output file directory", e);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
                RecursiveVisitor visitor = getVisitor(writer, messageDigest);

                String filename;
                while ((filename = reader.readLine()) != null) {
                    try {
                        try {
                            Path filePath = Path.of(filename);
                            Files.walkFileTree(filePath, visitor);
                        } catch (InvalidPathException e) {
                            visitor.writeEmptyHash(filename);
                        }
                    } catch (IOException e) {
                        throw new WalkException("Unexpected problem while writing in output file", e);
                    }
                }
            } catch (IOException e) {
                throw new WalkException("Unable to open output file", e);
            }
        } catch (IOException e) {
            throw new WalkException("Unable to open input file", e);
        }
    }



}
