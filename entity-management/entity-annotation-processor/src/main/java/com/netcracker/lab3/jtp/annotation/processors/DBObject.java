package com.netcracker.lab3.jtp.annotation.processors;


import com.netcracker.lab3.jtp.annotation.DBObjectType;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import java.math.BigInteger;

import static java.util.Objects.isNull;


public class DBObject {
    final private BigInteger typeId;
    final private String name;
    final private BigInteger parentId;
    final private TypeElement annotatedClass;
    final private DBObjectType objectType;

    public DBObject(Element element) {
        annotatedClass = (TypeElement) element;
        objectType = element.getAnnotation(DBObjectType.class);
        DeclaredType declared = (DeclaredType) annotatedClass.getSuperclass();
        Element supertypeElement = declared.asElement();
        DBObjectType parent = supertypeElement.getAnnotation(DBObjectType.class);
        typeId = BigInteger.valueOf(objectType.id());
        name = element.getSimpleName().toString();
        parentId = isNull(parent) ? null : BigInteger.valueOf(parent.id());
    }

    public BigInteger getTypeId() {
        return typeId;
    }

    public String getName() {
        return name;
    }

    public BigInteger getParentId() {
        return parentId;
    }

    public TypeElement getAnnotatedClass() {
        return annotatedClass;
    }

    public DBObjectType getObjectType() {
        return objectType;
    }
}
