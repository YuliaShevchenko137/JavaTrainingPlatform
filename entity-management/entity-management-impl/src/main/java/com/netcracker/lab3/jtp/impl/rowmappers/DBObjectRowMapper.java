package com.netcracker.lab3.jtp.impl.rowmappers;

import com.netcracker.lab3.jtp.entity.Entity;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.util.Objects.nonNull;

public class DBObjectRowMapper implements RowMapper {
    @Override
    public Entity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Entity entity = null;
        try {
            entity = (Entity) Class.forName("com.netcracker.lab3.jtp.entity." + resultSet.getString(1)).newInstance();
            entity.setId(new BigInteger(resultSet.getString(2)));
            if(nonNull(resultSet.getString(3))) {
                entity.setParentId(new BigInteger(resultSet.getString(3)));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return entity;
    }
}
