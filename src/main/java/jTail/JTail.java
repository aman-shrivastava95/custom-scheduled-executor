package jTail;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.*;

public class JTail {
    private final String fileName ;
    private final int noOfLines ;
    private Path watchDirectory ;
    public  JTail(final String fileName, final int noOfLines){
        if (fileName == null || fileName.isEmpty() || noOfLines <=0){
            throw new IllegalStateException(String.format("invalid [filename:%s], noOfLines:%d", fileName, noOfLines));

        }
        this.fileName = fileName ;
        this.noOfLines = noOfLines ;
    }

    public void setWatchDirectory(final Path watchDirectory){
        this.watchDirectory = watchDirectory ;
    }

    public String tailStatic() throws IOException {
        try (final var filePtr = new RandomAccessFile(fileName, "r")){
            final long fileSize = filePtr.length() ;
            filePtr.seek(fileSize - 1);

            long newLineCount = 0L ;
            final var lastNLines = new StringBuilder() ;

            //read file in reverse and look for line separator '\n'
            for (long i = fileSize - 1; i!=-1; i--){
                filePtr.seek(i);
                final int readByte = filePtr.readByte() ;
                final char c  = (char)readByte ;
                if ( (c == '\n') || (System.lineSeparator().equals(String.valueOf(c)))) {
                    ++newLineCount ;
                    if(newLineCount > noOfLines){
                        break ;
                    }
                }
                lastNLines.append(c) ;
            }
            lastNLines.reverse() ;
            return lastNLines.toString() ;
        }
    }

    public void tailOnline() throws IOException, InterruptedException {
        if(watchDirectory == null) return ;
        final var watchService = FileSystems.getDefault().newWatchService() ;
        watchDirectory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY) ;
        WatchKey key;
        while((key = watchService.take()) != null){
            for(final WatchEvent<?> event : key.pollEvents()){
                System.out.printf("%s", tailStatic());
            }
            key.reset();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        JTail tail = new JTail("src/main/resources/ques.txt", 1) ;
        tail.setWatchDirectory(Path.of("src", "main", "resources"));
        tail.tailOnline();
    }
}
