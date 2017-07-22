package com.netcracker.lab3.jtp.file;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static java.util.Objects.nonNull;

public class FileWriter {
    public static void appendResourcesFile(String resourcePath, String content) throws IOException{
        Files.write(FileFinder.getPathToRecourse(resourcePath), content.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.WRITE);
    }

    public static void writeResourcesFile(String resourcePath, String content) throws IOException{
        Files.write(FileFinder.getPathToRecourse(resourcePath), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    public static void appendFile(String filePath, String content) throws IOException{
        Files.write(FileFinder.getPath(filePath), content.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.WRITE);
    }

    public static void writeFile(String filePath, String content) throws IOException{
        Files.write(FileFinder.getPath(filePath), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    public static void writeFile(String filePath, InputStream input) throws IOException {
        ByteArrayOutputStream bos = null;
        try {
            byte[] buffer = new byte[1024];
            bos = new ByteArrayOutputStream();
            for (int len; (len = input.read(buffer)) != -1; ) {
                bos.write(buffer, 0, len);
            }
            Files.write(FileFinder.getPath(filePath), bos.toByteArray(), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } finally {
            if(nonNull(bos)) {
                bos.close();
            }
            input.close();
        }
    }

}
