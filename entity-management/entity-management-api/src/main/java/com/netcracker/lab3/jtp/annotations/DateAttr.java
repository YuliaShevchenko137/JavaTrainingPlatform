package com.netcracker.lab3.jtp.annotations;

import java.lang.annotation.*;

/**
 * This annotation indicates that the current field has an date value.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DateAttr {
}
