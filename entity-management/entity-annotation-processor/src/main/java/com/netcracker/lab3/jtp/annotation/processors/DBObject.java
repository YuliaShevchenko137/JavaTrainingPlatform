package com.netcracker.lab3.jtp.annotation.processors;


import com.netcracker.lab3.jtp.annotation.DBObjectType;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import java.math.BigInteger;

import static java.util.Objects.isNull;

public class DBObject {
    private BigInteger id;
    private String name;
    private BigInteger parentId;
    private TypeElement anClass;
    private DBObjectType objectType;

    public DBObject(Element element) {
        anClass = (TypeElement) element;
        objectType = element.getAnnotation(DBObjectType.class);
        DeclaredType declared = (DeclaredType) anClass.getSuperclass();
        Element supertypeElement = declared.asElement();
        DBObjectType parent = supertypeElement.getAnnotation(DBObjectType.class);
        id = BigInteger.valueOf(objectType.id());
        name = element.getSimpleName().toString();
        parentId = isNull(parent) ? null : BigInteger.valueOf(parent.id());
    }

    public BigInteger getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigInteger getParentId() {
        return parentId;
    }

    public TypeElement getAnClass() {
        return anClass;
    }

    public DBObjectType getObjectType() {
        return objectType;
    }
}
