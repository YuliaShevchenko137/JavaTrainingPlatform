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
import com.netcracker.lab3.jtp.impl.rowmappers.DataRowMapper;
import com.netcracker.lab3.jtp.impl.rowmappers.ParameterRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@SuppressWarnings({"PMD.ShortVariable"})
public class EntityManagerImpl implements EntityManager {
    private final SpringDataBase dataBase;
    private final static String ATTRIBUTE_BY_OBJECT_AND_TYPE = "attributeByObjectAndType.txt";
    private final static String OBJECT_BY_ID = "objectById.txt";
    private final static String OBJECT_BY_PARAMETER = "objectById.txt";
    private final static String OBJECTS_BY_PARENT = "objectsByPARENT.txt";
    private final static String OBJECTS_BY_TYPE = "objectsByType.txt";
    private final static String PARAMETERS_BY_OBJECT = "parametersByObject.txt";
    private final static String PARAMETER_BY_OBJECT_AND_ATTRIBUTE = "parametersByAttributeAndObject.txt";
    private final static String PARAMETER_BY_ID = "parametersByID.txt";
    private final static String BLOB_BY_OBJECT_AND_ATTRIBUTE = "blobByAttributeAndObject.txt";
    private final static String BLOB_BY_OBJECT = "blobByObject.txt";
    private final static String BLOB_BY_ID = "blobByID.txt";
    private final static String UPDATE_PARAMETER = "updateParameter.txt";
    private final static String UPDATE_BLOB = "updateBlob.txt";
    private final static String INSERT_OBJECT = "insertObject.txt";
    private final static String INSERT_PARAMETER = "insertParameter.txt";
    private final static String INSERT_BLOB = "insertBlob.txt";
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

    public Object getParameterById(BigInteger id) {
        Entity entity = null;
        ParametrImpl parametr = null;
        try {
            entity = (Entity) dataBase.executeObjectQuery(
                    String.format(FileReader.readFile(getClass().getPackage(), OBJECT_BY_PARAMETER), id.toString()),
                    new DBObjectRowMapper());
            parametr = (ParametrImpl) dataBase.executeListQuery(
                    String.format(FileReader.readFile(getClass().getPackage(), PARAMETER_BY_ID), id.toString()),
                    new ParameterRowMapper());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Field field = entity.getField(parametr.getName());
        if(field.getAnnotation(Attribute.class).value().equals(AttributeType.Data)) {
            try {
                return dataBase.executeObjectQuery(
                        String.format(FileReader.readFile(getClass().getPackage(),
                                BLOB_BY_ID), id),
                        new DataRowMapper());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return DataConverter.dataConvert(field.getType(), parametr.getValue());
    }

    public ParametrReferencesImpl getParameterReference(String refernces) {
        Entity entity;
        ArrayList<ParametrImpl> parametrs;
        ParametrReferencesImpl parametrReferences = null;
        String[] ides = refernces.split(" ");
        parametrReferences.setObjectId(new BigInteger(ides[0]));
        parametrReferences.setAttributeId(new BigInteger(ides[1]));
        try {
            entity = (Entity) dataBase.executeObjectQuery(
                    String.format(FileReader.readFile(getClass().getPackage(), OBJECT_BY_PARAMETER),
                            parametrReferences.getObjectId().toString()),
                    new DBObjectRowMapper());
            parametrs = new ArrayList<>(dataBase.executeListQuery(
                    String.format(FileReader.readFile(getClass().getPackage(), PARAMETER_BY_OBJECT_AND_ATTRIBUTE),
                            parametrReferences.getObjectId().toString(), parametrReferences.getAttributeId().toString()),
                    new ParameterRowMapper()));
            Field field = entity.getField(parametrs.get(0).getName());
            if (field.getAnnotation(Attribute.class).value().equals(AttributeType.Data)) {
                parametrReferences.setValue(dataBase.executeObjectQuery(
                        String.format(FileReader.readFile(getClass().getPackage(),
                                BLOB_BY_OBJECT_AND_ATTRIBUTE), parametrReferences.getObjectId(),
                                parametrReferences.getAttributeId()),
                        new DataRowMapper()));
            } else if (field.getAnnotation(Attribute.class).value().equals(AttributeType.List)) {
                for (ParametrImpl parametr : parametrs) {
                    setListValue(entity, field, parametr.getValue());
                }
            } else {
                parametrReferences.setValue(DataConverter.dataConvert(field, parametrs.get(0).getValue()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parametrReferences;
    }

    public void setListValue(Entity entity, Field field, String value){
        try {
            if (isNull(field.get(entity))) {
                field.set(entity, new ArrayList<>());
            }
            ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
            Class<?> listClass = (Class<?>) stringListType.getActualTypeArguments()[0];
            ((ArrayList) field.get(entity)).add(DataConverter.dataConvert(listClass,value));
        } catch (IllegalAccessException e) {
            log.error(e.getMessage());
        }
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
            for (ParametrImpl parametr : parametrs) {
                Field field = entity.getField(parametr.getName());
                if (nonNull(parametr.getValue())) {
                    if (field.getAnnotation(Attribute.class).value().equals(AttributeType.List)) {
                        setListValue(entity, field, parametr.getValue());
                    } else {
                        entity.setValue(field, DataConverter.dataConvert(field, parametr.getValue()));
                    }
                } else {
                    if (field.getAnnotation(Attribute.class).value().equals(AttributeType.Data)) {
                        entity.setValue(field,
                                dataBase.executeObjectQuery(
                                        String.format(FileReader.readFile(getClass().getPackage(),
                                                BLOB_BY_OBJECT), entity.getId().toString()),
                                        new DataRowMapper()));
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
    public List<Entity> getObjectsByType(BigInteger typeId) {
        ArrayList<Entity> entities = null;
        try {
            entities = new ArrayList<>(dataBase.executeListQuery(
                    String.format(FileReader.readFile(getClass().getPackage(), OBJECTS_BY_TYPE), typeId.toString()),
                    new DBObjectRowMapper()));
            for (Entity entity : entities) {
                createObject(entity);
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
            for (BigInteger id : ides) {
                entities.add(getObjectById(id));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entities;
    }

    @Override
    public void update(Entity entity) {
        ArrayList<Field> fields = new ArrayList<>(entity.getFields());
        for (Field field : fields) {
            updateParametr(entity, field);
        }
    }

    public void updateParametr(Entity entity, Field field){
        if(field.getAnnotation(Attribute.class).value().equals(AttributeType.List)) {
            deleteParametr(entity, field);
            insertParametr(entity, field);
        } else if(field.getAnnotation(Attribute.class).value().equals(AttributeType.Data)) {
            try {
                PreparedStatement preparedStatement = dataBase.getPreparedStatement(
                        String.format(FileReader.readFile(this.getClass().getPackage(), UPDATE_BLOB),
                                field.getName(), field.getAnnotation(Attribute.class).value().name(), entity.getId()));
                preparedStatement.setBytes(1, FileReader.streamToBytes((InputStream) entity.getValue(field)));
                preparedStatement.execute();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                dataBase.execute(String.format(FileReader.readFile(getClass().getPackage(), UPDATE_PARAMETER),
                        field.getAnnotation(Attribute.class).value().name(), DataConverter.convertToData(entity, field),
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
        for (Field field : fields) {
            insertParametr(entity, field);
        }
    }

    public void insertParametr(Entity entity, Field field){
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
                insertList((List) field.get(entity), entity.getId(), attrId);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
            }
        } else if(field.getAnnotation(Attribute.class).value().equals(AttributeType.Data)) {
            try {
                PreparedStatement preparedStatement = dataBase.getPreparedStatement(
                        String.format(FileReader.readFile(this.getClass().getPackage(), INSERT_BLOB),
                                KeyGenerator.generate(), entity.getId(), attrId));
                preparedStatement.setBytes(1, FileReader.streamToBytes((InputStream) entity.getValue(field)));
                preparedStatement.execute();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                dataBase.execute(
                        String.format(FileReader.readFile(getClass().getPackage(), INSERT_PARAMETER),
                                field.getAnnotation(Attribute.class).value().name(), KeyGenerator.generate(),
                                entity.getId(), attrId, DataConverter.convertToData(entity, field)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertList(List parameters, BigInteger objectId, BigInteger attrId){
        if(nonNull(parameters)) {
            for (Object listElement : parameters) {
                try {
                    dataBase.execute(String.format(FileReader.readFile(getClass().getPackage(), INSERT_PARAMETER),
                            AttributeType.List.name(), objectId, KeyGenerator.generate(), attrId,
                            DataConverter.convertToData(listElement)));
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