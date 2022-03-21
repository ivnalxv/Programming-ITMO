package info.kgeorgiy.ja.alekseev.walk;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.HexFormat;

public class RecursiveVisitor extends SimpleFileVisitor<Path> {
    private final Writer writer;
    private final byte[] buffer = new byte[4096];
    private final static HexFormat hexFormatter = HexFormat.of();
    private static MessageDigest messageDigest;
    private final static byte[] ZERO = new byte[20];

    public RecursiveVisitor(Writer writer, MessageDigest messageDigest) {
        RecursiveVisitor.messageDigest = messageDigest;
        this.writer = writer;
    }

    public void writeEmptyHash(String filename) throws IOException {
        writeHash(filename, ZERO);
    }

    public void writeHash(String filename, byte[] hash) throws IOException {
        writer.write(String.format("%s %s%n", hexFormatter.formatHex(hash), filename));
    }

    private byte[] hashFile(Path filePath) {
        try (InputStream reader = new FileInputStream(filePath.toFile())) {
                int size;
                while ((size = reader.read(buffer)) >= 0) {
                    messageDigest.update(buffer, 0, size);
                }
                return messageDigest.digest();
        } catch (IOException e) {
            return ZERO;
        }
    }

    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        writeHash(file.toString(), hashFile(file));
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
        writeEmptyHash(file.toString());
        return FileVisitResult.CONTINUE;
    }
}
