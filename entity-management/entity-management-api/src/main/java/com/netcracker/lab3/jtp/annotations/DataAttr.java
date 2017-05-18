package com.netcracker.lab3.jtp.annotations;

import java.lang.annotation.*;

/**
 * This annotation indicates that the current field has a large set of byte data.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataAttr {
}
