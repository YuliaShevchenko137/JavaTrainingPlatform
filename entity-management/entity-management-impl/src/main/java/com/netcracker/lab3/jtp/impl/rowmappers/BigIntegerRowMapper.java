package com.netcracker.lab3.jtp.impl.rowmappers;

import org.springframework.jdbc.core.RowMapper;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BigIntegerRowMapper implements RowMapper{
    @Override
    public BigInteger mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new BigInteger(resultSet.getString(1));
    }
}
