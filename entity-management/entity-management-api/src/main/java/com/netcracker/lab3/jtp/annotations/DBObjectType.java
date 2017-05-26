package com.netcracker.lab3.jtp.annotations;

import java.lang.annotation.*;

@Inherited
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface DBObjectType {
}
