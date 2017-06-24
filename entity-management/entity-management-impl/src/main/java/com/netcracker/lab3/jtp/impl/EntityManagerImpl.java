package com.netcracker.lab3.jtp.impl;

import com.netcracker.lab3.jtp.*;
import com.netcracker.lab3.jtp.annotation.Attribute;
import com.netcracker.lab3.jtp.enums.AttributeType;
import com.netcracker.lab3.jtp.annotation.DBObjectType;
import com.netcracker.lab3.jtp.api.EntityManager;
import com.netcracker.lab3.jtp.db.SpringDataBase;
import com.netcracker.lab3.jtp.entity.Entity;
import com.netcracker.lab3.jtp.rowmappers.BigIntegerRowMapper;
import com.netcracker.lab3.jtp.rowmappers.DBObjectRowMapper;
import com.netcracker.lab3.jtp.rowmappers.ParameterRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
public class EntityManagerImpl implements EntityManager {
    SpringDataBase dataBase;

    public  EntityManagerImpl(){
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans/DataBaseBeans.xml");
        dataBase = (SpringDataBase) context.getBean("dataBase");
    }

    public EntityManagerImpl(SpringDataBase dataBase){
        this.dataBase = dataBase;
    }

    @Override
    public Object getParameterById(BigInteger id) {
        Entity entity = (Entity) dataBase.executeObjectQuery(
                "select type.name from dbobjects entity join object_types type" +
                        "on (entity.object_type_id = type.object_type_id) " +
                        "join parameters params on (params.object_id = entity.object_id) where params.PARAMETER_ID = " + id,
                new DBObjectRowMapper());
        ParametrImpl parametr = (ParametrImpl) dataBase.executeListQuery(
                "select attr.name, " +
                        "COALESCE(to_char(INTEGER_VALUE)," +
                        "to_char(DECIMAL_VALUE)," +
                        "STRING_VALUE," +
                        "to_char(XML_VALUE)," +
                        "to_char(DATE_VALUE, 'yyyy mm dd hh24 mm ss')," +
                        "to_char(LIST_ID)," +
                        "to_char(REFERENCES_OBJECT)," +
                        "to_char(REFERENCES_PARAM)," +
                        "DATA_VALUE)" +
                        " from attributes attr join PARAMETERS params" +
                        "on (attr.attribute_type = params.attribute_type) where params.PARAMETER_ID = " + id,
                new ParameterRowMapper());
        DataConverter conv = new DataConverter();
        Field field = entity.getField(parametr.getName());
        return conv.dataConvert(field.getType(),parametr.getValue());
    }

    @Override
    public Entity getObjectById(BigInteger id) {
        Entity entity = (Entity) dataBase.executeObjectQuery(
                "select type.name from dbobjects entity join object_types " +
                        "on (entity.object_type_id = type.object_type_id) where entity.obj_ID = " + id,
                new DBObjectRowMapper());
        entity.setId(id);
        return createObject(entity);

    }

    @Override
    public Entity createObject(Entity entity) {
        ArrayList<ParametrImpl> parametrs = new ArrayList<>(dataBase.executeListQuery(
                "select attr.name, " +
                        "COALESCE(to_char(INTEGER_VALUE)," +
                        "to_char(DECIMAL_VALUE)," +
                        "STRING_VALUE," +
                        "to_char(XML_VALUE)," +
                        "to_char(DATE_VALUE, 'yyyy mm dd hh24 mi ss')," +
                        "to_char(LIST_ID)," +
                        "to_char(REFERENCES_OBJECT)," +
                        "to_char(REFERENCES_PARAM)," +
                        "DATA_VALUE)" +
                        " from attributes attr join PARAMETERS params" +
                        "on (attr.attribute_type = params.attribute_type) where params.object_id = " + entity.getId(),
                new ParameterRowMapper()));
        DataConverter conv = new DataConverter();
        for (int i = 0; i < parametrs.size(); i++) {
            Field field = entity.getField(parametrs.get(i).getName());
            if ((field.getAnnotation(Attribute.class)).value().equals(AttributeType.List)) {
                try {
                    if (isNull(field.get(entity))) {
                        field.set(entity, new ArrayList<>());
                    }
                    ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                    Class<?> listClass = (Class<?>) stringListType.getActualTypeArguments()[0];
                    ((ArrayList)field.get(entity)).add(conv.dataConvert(listClass,parametrs.get(i).getValue()));
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage());
                }
            } else {
                entity.setValue(field, conv.dataConvert(field.getType(), parametrs.get(i).getValue()));
            }
        }
        return entity;
    }

    @Override
    public List<Entity> getObjectsByType(BigInteger id) {
        ArrayList<Entity> entities = new ArrayList<>(dataBase.executeListQuery(
                "select type.name from dbobjects entity join object_types type " +
                        "on (entity.object_type_id = type.object_type_id) where entity.object_ID = " + id,
                new DBObjectRowMapper()));
        for (int i = 0; i < entities.size(); i++) {
            createObject(entities.get(i));
        }
        return entities;
    }

    @Override
    public List<Entity> getObjectsByType(Class entityClass) {
        return getObjectsByType(BigInteger.valueOf(((DBObjectType)entityClass.getAnnotation(DBObjectType.class)).id()));
    }

    @Override
    public List<Entity> getObjectChilds(Entity entity) {
        ArrayList<BigInteger> ides = new ArrayList<>(dataBase.executeListQuery(
                "select object_id from dbobjects where parent_id = " + entity.getId(),
                new BigIntegerRowMapper()));
        ArrayList<Entity> entities = new ArrayList<>();
        for (int i = 0; i < ides.size(); i++) {
            entities.add(getObjectById(ides.get(i)));
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
            dataBase.execute( "update parameters set "
                    + field.getAnnotation(Attribute.class).value().name() + "_value=" +
                    converter.convertToData(entity, field) +
                    " where attribute_id = (select attribute_id from attributes where name = '" + field.getName() +
                    "' and type_name = '" + field.getAnnotation(Attribute.class).value().name() + "') and" +
                    " object_id = " + entity.getId());
        }
    }

    @Override
    public void insert(Entity entity) {
        entity.setId( KeyGenerator.generate());
        dataBase.execute("insert into dbobjects values(" + entity.getId() + "," +
                entity.getObjectTypeId() + "," +
                entity.getParentId() + ")");
        ArrayList<Field> fields = new ArrayList<>(entity.getFields());
        for (int i = 0; i < fields.size(); i++) {
           insertParametr(entity, fields.get(i));
        }
    }

    public void insertParametr(Entity entity, Field field){
        DataConverter converter = new DataConverter();
        BigInteger attrId = (BigInteger) dataBase.executeObjectQuery("select attribute_id from attributes where name = '" + field.getName() + "' and type_name = '" +
                field.getAnnotation(Attribute.class).value().name() + "'", new BigIntegerRowMapper());
        String request = "insert into parameters(PARAMETER_ID, object_id, attribute_id, "
                + field.getAnnotation(Attribute.class).value().name() + "_value) values(" +
                KeyGenerator.generate() + "," +
                entity.getId() + "," +
                attrId + ",";
        if(field.getAnnotation(Attribute.class).value().equals(AttributeType.List)) {
            try {
                insertList((List) field.get(entity));
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
            }
        } else {
            dataBase.execute( request + converter.convertToData(entity, field) + ")");
        }
    }

    public void insertList(List list){
        BigInteger id;
        DataConverter converter = new DataConverter();
        for (int i = 0; i < list.size(); i++) {
            id = KeyGenerator.generate();
            BigInteger attrId = (BigInteger) dataBase.executeObjectQuery("select attribute_id from attributes where name = 'list' and type_name = '" +
                    list.get(i).getClass().getName() + "'", new BigIntegerRowMapper());
            dataBase.execute("insert into LIST_TYPE values(" + id + ", " + converter.convertToData(list.get(i)) + "," +
                    attrId + ")");
        }
    }

    @Override
    public void delete(Entity entity) {
        dataBase.execute("delete dbobjects where object_id = " + entity.getId());
        dataBase.execute("delete PARAMETERS where object_id = " + entity.getId());
    }

    public void deleteParametr(Entity entity, Field field) {
        dataBase.execute("delete PARAMETERS where object_id = " + entity.getId() +
                " and attribute_id = (select attribute_id from attributes where name = '" + field.getName() +
                "' and type_name = '" + field.getAnnotation(Attribute.class).value().name() + "')");
    }

    @Override
    public Connection getConnection() {
        return dataBase.getConnection();
    }
}
