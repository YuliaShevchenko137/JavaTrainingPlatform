package com.netcracker.lab3.jtp.annotations;

import java.lang.annotation.*;

/**
 * This annotation indicates that the value of the current field is the id of the reference to the object.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RefObjectIdAttr {
}
