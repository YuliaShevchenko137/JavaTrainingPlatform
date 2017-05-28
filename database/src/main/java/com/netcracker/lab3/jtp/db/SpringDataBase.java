package com.netcracker.lab3.jtp.db;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class SpringDataBase implements DAO{
    private DataSource dataSource;
    final private JdbcTemplate jdbcTemplateObject;

    public SpringDataBase(){
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans/DataBaseBeans.xml");
        dataSource = (DriverManagerDataSource) context.getBean("DataSource");
        jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    public SpringDataBase(DataSource dataSource){
        jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    public SpringDataBase(String beanName){
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans/DataBaseBeans.xml");
        dataSource = (DataSource) context.getBean(beanName);
        jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    @Override
    public Connection getConnection() {
        Connection con = null;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            log.debug(e.getMessage());
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
            log.debug(e.getMessage());
        }
    }

    @Override
    public void setAutoCommit(boolean isAuto) {
        try {
            dataSource.getConnection().setAutoCommit(isAuto);
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }
    }

    @Override
    public void commit() {
        try {
            dataSource.getConnection().commit();
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }
    }

    @Override
    public void rollback() {
        try {
            dataSource.getConnection().rollback();
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }
    }
}
