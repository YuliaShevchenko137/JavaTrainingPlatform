package com.netcracker.lab3.jtp.annotations;

import java.lang.annotation.*;

/**
 * This annotation indicates that the value of the current field is a list of auxiliary objects.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListAttr {
}
