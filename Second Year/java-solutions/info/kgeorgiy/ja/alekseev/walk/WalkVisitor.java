package info.kgeorgiy.ja.alekseev.walk;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;

public class WalkVisitor extends RecursiveVisitor {
    public WalkVisitor(Writer writer, MessageDigest messageDigest) {
        super(writer, messageDigest);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return visitFileFailed(dir, null);
    }
}
