package com.netcracker.lab3.jtp.file;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

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

}
