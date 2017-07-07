package com.netcracker.lab3.jtp.impl;

import com.netcracker.lab3.jtp.*;
import com.netcracker.lab3.jtp.annotation.Attribute;
import com.netcracker.lab3.jtp.enums.AttributeType;
import com.netcracker.lab3.jtp.annotation.DBObjectType;
import com.netcracker.lab3.jtp.api.EntityManager;
import com.netcracker.lab3.jtp.db.SpringDataBase;
import com.netcracker.lab3.jtp.entity.Entity;
import com.netcracker.lab3.jtp.file.FileReader;
import com.netcracker.lab3.jtp.impl.rowmappers.BigIntegerRowMapper;
import com.netcracker.lab3.jtp.impl.rowmappers.DBObjectRowMapper;
import com.netcracker.lab3.jtp.impl.rowmappers.ParameterRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
public class EntityManagerImpl implements EntityManager {
    SpringDataBase dataBase;
    private final static String ATTRIBUTE_BY_OBJECT_AND_TYPE = "attributeByObjectAndType.txt";
    private final static String OBJECT_BY_ID = "objectById.txt";
    private final static String OBJECT_BY_PARAMETER = "objectById.txt";
    private final static String OBJECTS_BY_PARENT = "objectsByPARENT.txt";
    private final static String OBJECTS_BY_TYPE = "objectsByType.txt";
    private final static String PARAMETERS_BY_OBJECT = "parametersByObject.txt";
    private final static String PARAMETERS_BY_ID = "parametersByID.txt";
    private final static String UPDATE_PARAMETER = "updateParameter.txt";
    private final static String INSERT_OBJECT = "insertObject.txt";
    private final static String INSERT_PARAMETER = "insertParameter.txt";
    private final static String INSERT_LIST = "insertList.txt";
    private final static String DELETE_OBJECT = "deleteObject.txt";
    private final static String DELETE_PARAMETERS = "deleteParametersByObject.txt";
    private final static String DELETE_PARAMETER = "deleteParameter.txt";

    public  EntityManagerImpl(){
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans/DataBaseBeans.xml");
        dataBase = (SpringDataBase) context.getBean("dataBase");
    }

    public EntityManagerImpl(SpringDataBase dataBase){
        this.dataBase = dataBase;
    }

    @Override
    public Object getParameterById(BigInteger id) {
        Entity entity = null;
        ParametrImpl parametr = null;
        try {
            entity = (Entity) dataBase.executeObjectQuery(
                    String.format(FileReader.readFile(getClass().getPackage(), OBJECT_BY_PARAMETER), id.toString()),
                    new DBObjectRowMapper());
            parametr = (ParametrImpl) dataBase.executeListQuery(
                    String.format(FileReader.readFile(getClass().getPackage(), PARAMETERS_BY_ID), id.toString()),
                    new ParameterRowMapper());
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataConverter conv = new DataConverter();
        Field field = entity.getField(parametr.getName());
        return conv.dataConvert(field.getType(), parametr.getValue());
    }

    @Override
    public Entity getObjectById(BigInteger id) {
        Entity entity = null;
        try {
            entity = (Entity) dataBase.executeObjectQuery(
                    String.format(FileReader.readFile(getClass().getPackage(), OBJECT_BY_ID), id.toString()),
                    new DBObjectRowMapper());
            entity.setId(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return createObject(entity);

    }

    @Override
    public Entity createObject(Entity entity) {
        try {
            ArrayList<ParametrImpl> parametrs = new ArrayList<>(dataBase.executeListQuery(
                    String.format(FileReader.readFile(getClass().getPackage(), PARAMETERS_BY_OBJECT), entity.getId().toString()),
                    new ParameterRowMapper()));
            DataConverter conv = new DataConverter();
            for (int i = 0; i < parametrs.size(); i++) {
                if (nonNull(parametrs.get(i).getValue())) {
                    Field field = entity.getField(parametrs.get(i).getName());
                    if (field.getAnnotation(Attribute.class).value().equals(AttributeType.List)) {
                        try {
                            if (isNull(field.get(entity))) {
                                field.set(entity, new ArrayList<>());
                            }
                            ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                            Class<?> listClass = (Class<?>) stringListType.getActualTypeArguments()[0];
                            ((ArrayList) field.get(entity)).add(conv.dataConvert(listClass, parametrs.get(i).getValue()));
                        } catch (IllegalAccessException e) {
                            log.error(e.getMessage());
                        }
                    } else {
                        entity.setValue(field, conv.dataConvert(field, parametrs.get(i).getValue()));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public void execute(String request) {
        dataBase.execute(request);
    }

    @Override
    public List<Entity> getObjectsByType(BigInteger id) {
        ArrayList<Entity> entities = null;
        try {
            entities = new ArrayList<>(dataBase.executeListQuery(
                    String.format(FileReader.readFile(getClass().getPackage(), OBJECTS_BY_TYPE), id.toString()),
                    new DBObjectRowMapper()));
            for (int i = 0; i < entities.size(); i++) {
                createObject(entities.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entities;
    }

    @Override
    public List<Entity> getObjectsByType(Class entityClass) {
        return getObjectsByType(BigInteger.valueOf(((DBObjectType)entityClass.getAnnotation(DBObjectType.class)).id()));
    }

    @Override
    public List<Entity> getObjectChilds(Entity entity) {
        ArrayList<Entity> entities = null;
        try {
            ArrayList<BigInteger> ides = new ArrayList<>(dataBase.executeListQuery(
                    String.format(FileReader.readFile(getClass().getPackage(), OBJECTS_BY_PARENT), entity.getId().toString()),
                    new BigIntegerRowMapper()));
            entities = new ArrayList<>();
            for (int i = 0; i < ides.size(); i++) {
                entities.add(getObjectById(ides.get(i)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entities;
    }

    @Override
    public void update(Entity entity) {
        ArrayList<Field> fields = new ArrayList<>(entity.getFields());
        for (int i = 0; i < fields.size(); i++) {
            updateParametr(entity, fields.get(i));
        }
    }

    public void updateParametr(Entity entity, Field field){
        if(field.getAnnotation(Attribute.class).value().equals(AttributeType.List)) {
            deleteParametr(entity, field);
            insertParametr(entity, field);
        } else {
            DataConverter converter = new DataConverter();
            try {
                dataBase.execute(String.format(FileReader.readFile(getClass().getPackage(), UPDATE_PARAMETER),
                        field.getAnnotation(Attribute.class).value().name(), converter.convertToData(entity, field),
                        field.getName(), field.getAnnotation(Attribute.class).value().name(), entity.getId()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void insert(Entity entity) {
        entity.setId(KeyGenerator.generate());
        try {
            dataBase.execute(String.format(FileReader.readFile(getClass().getPackage(), INSERT_OBJECT), entity.getId(),
                    entity.getObjectTypeId(), entity.getParentId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<Field> fields = new ArrayList<>(entity.getFields());
        for (int i = 0; i < fields.size(); i++) {
            insertParametr(entity, fields.get(i));
        }
    }

    public void insertParametr(Entity entity, Field field){
        DataConverter converter = new DataConverter();
        BigInteger attrId = null;
        try {
            attrId = (BigInteger) dataBase.executeObjectQuery(
                    String.format(FileReader.readFile(getClass().getPackage(), ATTRIBUTE_BY_OBJECT_AND_TYPE), field.getName(),
                            field.getAnnotation(Attribute.class).value().name()), new BigIntegerRowMapper());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(field.getAnnotation(Attribute.class).value().equals(AttributeType.List)) {
            try {
                insertList((List) field.get(entity));
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
            }
        } else {
            try {
                dataBase.execute(
                        String.format(FileReader.readFile(getClass().getPackage(), INSERT_PARAMETER),
                                field.getAnnotation(Attribute.class).value().name(), KeyGenerator.generate(),
                                entity.getId(), attrId, converter.convertToData(entity, field)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertList(List list){
        BigInteger id;
        DataConverter converter = new DataConverter();
        if(nonNull(list)) {
            for (int i = 0; i < list.size(); i++) {
                id = KeyGenerator.generate();
                BigInteger attrId = null;
                try {

                    attrId = (BigInteger) dataBase.executeObjectQuery(String.format(FileReader.readFile(getClass().getPackage(), ATTRIBUTE_BY_OBJECT_AND_TYPE),
                            "list", AttributeType.List.name()), new BigIntegerRowMapper()); //доработать

                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    dataBase.execute(String.format(FileReader.readFile(getClass().getPackage(), INSERT_LIST), id.toString(),
                            converter.convertToData(list.get(i)), attrId));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void delete(Entity entity) {
        try {
            dataBase.execute(String.format(FileReader.readFile(getClass().getPackage(), DELETE_OBJECT), entity.getId()));
            dataBase.execute(String.format(FileReader.readFile(getClass().getPackage(), DELETE_PARAMETERS), entity.getId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteParametr(Entity entity, Field field) {
        try {
            dataBase.execute(String.format(FileReader.readFile(getClass().getPackage(), DELETE_PARAMETER), entity.getId().toString(),
                    field.getName(), field.getAnnotation(Attribute.class).value().name()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        return dataBase.getConnection();
    }
}