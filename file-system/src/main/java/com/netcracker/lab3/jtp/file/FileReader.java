package com.netcracker.lab3.jtp.file;


import java.io.*;
import java.nio.file.Files;
import java.util.List;

import static java.util.Objects.nonNull;

public class FileReader {

    public static String readFile(Package path, String name) throws IOException{
        return listToString(Files.readAllLines(FileFinder.getPathToRecourse(path, name)));
    }

    public static String readFile(String path) throws IOException{
        return listToString(Files.readAllLines(FileFinder.getPath(path)));
    }

    public static InputStream fileToStream(String path) throws IOException{
        return Files.newInputStream(FileFinder.getPath(path));
    }

    public static byte[] streamToBytes(InputStream input) throws IOException {
        ByteArrayOutputStream bos = null;
        byte[] bytes;
        try {
            byte[] buffer = new byte[1024];
            bos = new ByteArrayOutputStream();
            for (int len; (len = input.read(buffer)) != -1; ) {
                bos.write(buffer, 0, len);
            }
            bytes = bos.toByteArray();
        } finally {
            if (nonNull(bos)) {
                bos.close();
            }
            input.close();
        }
        return bytes;
    }

    public static byte[] fileToBytes(String path) throws IOException {
        return streamToBytes(fileToStream(path));
    }

    private static String listToString(List<String> strings){
        StringBuilder builder = new StringBuilder();
        for (String str : strings) {
            builder.append(str).append('\n');
        }
        return builder.toString();
    }
}