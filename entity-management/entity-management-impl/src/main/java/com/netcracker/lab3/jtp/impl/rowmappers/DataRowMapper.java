package com.netcracker.lab3.jtp.impl.rowmappers;

import org.springframework.jdbc.core.RowMapper;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataRowMapper implements RowMapper {
    @Override
    public InputStream mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getBinaryStream(1);
    }
}
