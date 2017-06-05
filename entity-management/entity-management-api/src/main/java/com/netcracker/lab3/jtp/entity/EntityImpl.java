package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotation.DBObjectType;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@DBObjectType(id = 10)
public class EntityImpl implements Entity {

    private BigInteger id;
    private BigInteger parentId;

    public void setValue(String fieldName, Object value){
        Field field = getField(fieldName);
        try {
            field.set(this, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setValue(Field field, Object value){
        try {
            field.set(this, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getValue(Field field) {
        Object object = null;
        try {
            object =  field.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
       this.id =id;
    }

    @Override
    public Field getField(String name) {
        return getField(name, this.getClass());
    }

    public Field getField(String name, Class entityClass){
        Field[] fields = entityClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if(fields[i].getName().equals(name)) {
                return fields[i];
            }
        }
        if(!entityClass.equals(Object.class)) {
            return getField(name, entityClass.getSuperclass());
        }
        return null;
    }

    public List<Field> getFields(){
        return getFields(this.getClass());
    }

    public List<Field> getFields(Class entityClass){
        List<Field> list = new LinkedList<>();
        Field[] fields = entityClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            list.add(fields[i]);
        }
        if(!entityClass.getSuperclass().equals(EntityImpl.class)) {
            list.addAll(getFields(entityClass.getSuperclass()));
        }
        return list;
    }

    @Override
    public BigInteger getObjectTypeId() {
        return BigInteger.valueOf(this.getClass().getAnnotation(DBObjectType.class).id());
    }

    @Override
    public BigInteger getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(BigInteger id) {
        parentId = id;
    }
}
