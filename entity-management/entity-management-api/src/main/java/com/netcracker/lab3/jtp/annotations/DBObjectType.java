package com.netcracker.lab3.jtp.annotations;

import java.lang.annotation.*;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface DBObjectType {
    int id();
}
