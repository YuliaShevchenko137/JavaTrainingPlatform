package com.netcracker.lab3.jtp.annotation;


import com.netcracker.lab3.jtp.enums.AttributeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Attribute {
    AttributeType value();
}
