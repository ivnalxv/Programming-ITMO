package info.kgeorgiy.ja.alekseev.walk;

import java.io.BufferedWriter;
import java.security.MessageDigest;

public class RecursiveWalk extends AbstractWalk {
    public static void main(String[] args) {
        AbstractWalk walker = new RecursiveWalk();
        walker.walk(args);
    }

    @Override
    protected RecursiveVisitor getVisitor(BufferedWriter writer, MessageDigest messageDigest) {
        return new RecursiveVisitor(writer, messageDigest);
    }
}
