package com.netcracker.lab3.jtp.db;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SpringDataBase implements DAO{
    final private DataSource dataSource;
    final private JdbcTemplate jdbcTemplateObject;

    public SpringDataBase(){   // change to using bean
        dataSource = new DriverManagerDataSource("oracle.jdbc.driver.OracleDriver", "javal3test", "javal3test");
        jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    public SpringDataBase(String url, String username, String password){
        dataSource = new DriverManagerDataSource(url, username, password);
        jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    @Override
    public Connection getConnection() {
        Connection con = null;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    @Override
    public void execute(String request) {
        jdbcTemplateObject.execute(request);
    }

    @Override
    public Object executeObjectQuery(String request, RowMapper rowMapper) {
        return jdbcTemplateObject.queryForObject(request, rowMapper);
    }

    @Override
    public List executeListQuery(String request, RowMapper rowMapper) {
        return jdbcTemplateObject.query(request, rowMapper);
    }

    @Override
    public void close() {
        try {
            dataSource.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAutoCommit(boolean isAuto) {
        try {
            dataSource.getConnection().setAutoCommit(isAuto);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void commit() {
        try {
            dataSource.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rollback() {
        try {
            dataSource.getConnection().rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
