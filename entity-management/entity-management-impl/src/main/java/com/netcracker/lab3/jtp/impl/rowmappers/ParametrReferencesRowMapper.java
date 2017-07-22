package com.netcracker.lab3.jtp.impl.rowmappers;


import com.netcracker.lab3.jtp.DataConverter;
import com.netcracker.lab3.jtp.ParametrReferencesImpl;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ParametrReferencesRowMapper implements RowMapper {
    @Override
    public ParametrReferencesImpl mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ParametrReferencesImpl parametr = new ParametrReferencesImpl();
//        String[] ides = resultSet.getString(1).split(" ");
//        parametr.setObjectId(new BigInteger(ides[0]));
//        parametr.setAttributeId(new BigInteger(ides[1]));
        DataConverter converter = new DataConverter();
        try {
            parametr.setValue(converter.dataConvert(parametr.getClass().getField("value"), resultSet.getString(1)));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return parametr;
    }
}
