package com.netcracker.lab3.jtp.file;


import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileReader {

    public static String readFile(Package path, String name) throws IOException{
        return listToString(Files.readAllLines(FileFinder.getPathToRecourse(path, name)));
    }

    public static String readFile(String path) throws IOException{
        return listToString(Files.readAllLines(FileFinder.getPath(path)));
    }

    private static String listToString(List<String> strings){
        StringBuilder builder = new StringBuilder();
        for (String str : strings) {
            builder.append(str).append('\n');
        }
        return builder.toString();
    }
}