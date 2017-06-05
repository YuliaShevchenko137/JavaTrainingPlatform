package com.netcracker.lab3.jtp.annotation;

import java.lang.annotation.*;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface DBObjectType {
    int id();
}
