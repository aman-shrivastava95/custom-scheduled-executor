package jTail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;

public class PeriodicFIleWriter implements Runnable{

    private final Path filePath ;
    private final AtomicInteger counter = new AtomicInteger() ;
    public PeriodicFIleWriter(final Path filePath) {
        this.filePath = filePath ;
        try {
            Files.writeString(filePath, String.format("This is the header line%n")) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        //schedule this thread using Scheduled thread pool executor
        final String strToWrite = String.format("This is the %d line. %n", counter.addAndGet(1)) ;
        final byte[] bytesToWrite = strToWrite.getBytes(StandardCharsets.UTF_8);
        try {
            Files.write(filePath, bytesToWrite, StandardOpenOption.APPEND) ;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
