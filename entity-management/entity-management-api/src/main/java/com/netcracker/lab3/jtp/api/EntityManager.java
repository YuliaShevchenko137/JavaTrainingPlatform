package com.netcracker.lab3.jtp.api;


import com.netcracker.lab3.jtp.entity.Entity;

import java.math.BigInteger;
import java.sql.Connection;
import java.util.List;

public interface EntityManager {

    Entity getObjectById(BigInteger id);

    List<Entity> getObjectsByType(BigInteger id);

    List<Entity> getObjectsByType(Class entityClass);

    List<Entity> getObjectChilds(Entity entity);

    void update(Entity entity);

    void insert(Entity entity);

    void delete(Entity entity);

    Connection getConnection();

    Object getParameterById(BigInteger id);

    Entity createObject(Entity entity);
}
