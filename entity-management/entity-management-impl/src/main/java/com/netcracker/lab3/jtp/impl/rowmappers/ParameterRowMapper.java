package com.netcracker.lab3.jtp.impl.rowmappers;

import com.netcracker.lab3.jtp.ParametrImpl;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ParameterRowMapper implements RowMapper{
    @Override
    public ParametrImpl mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ParametrImpl parametr = new ParametrImpl();
        parametr.setName(resultSet.getString(1));
        parametr.setValue(resultSet.getString(2));
        return parametr;
    }
}
