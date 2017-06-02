package com.netcracker.lab3.jtp.entity;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.List;

public interface Entity {
    void setValue(String fieldName, Object value);

    Field getField(String name);

    void setValue(Field field, Object value);

    Object getValue(Field field);

    BigInteger getId();

    void setId(BigInteger id);

    List<Field> getFields();

    BigInteger getObjectTypeId();

    BigInteger getParentId();

    void setParentId(BigInteger id);
}
