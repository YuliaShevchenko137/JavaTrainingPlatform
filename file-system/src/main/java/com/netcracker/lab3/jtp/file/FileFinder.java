package com.netcracker.lab3.jtp.file;

import java.net.URISyntaxException;
import java.nio.file.*;

public class FileFinder {

    public static Path getPathToRecourse(Package classPackage, String name){
        Path path = null;
        try {
            path = Paths.get(Thread.currentThread().getContextClassLoader()
                    .getSystemResource(classPackage.getName() + "/" + name).toURI()
            );
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return path;
        // how to do with null pointer
    }

    public static Path getPath(String path){
        return Paths.get(path);
    }

    public static Path getPathToRecourse(String path){
        return Paths.get(Thread.currentThread().getContextClassLoader()
                .getResource(path).getPath());
    }
}
