package com.netcracker.lab3.jtp.db;

import org.springframework.jdbc.core.RowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public interface DAO {
    Connection getConnection();

    void execute(String request);

    void executeLiquibase(String path);

    Object executeObjectQuery(String request, RowMapper rowMapper);

    List executeListQuery(String request, RowMapper rowMapper);

    PreparedStatement getPreparedStatement(String request);

    void close();

    void setAutoCommit(boolean isAuto);

    void commit();

    void rollback();
}
