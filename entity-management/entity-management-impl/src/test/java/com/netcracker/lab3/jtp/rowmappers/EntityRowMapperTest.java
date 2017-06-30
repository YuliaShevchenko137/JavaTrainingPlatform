package com.netcracker.lab3.jtp.rowmappers;

import com.netcracker.lab3.jtp.db.SpringDataBase;
import com.netcracker.lab3.jtp.entity.DBObject;
import com.netcracker.lab3.jtp.entity.Entity;
import junit.framework.TestCase;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.FileSystemResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigInteger;

@Slf4j
@SuppressWarnings({"PMD.AvoidCatchingGenericException"})
public class EntityRowMapperTest extends TestCase{
    private final SpringDataBase springDataBase;

    public EntityRowMapperTest(){
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans/EntityManagerBeans.xml");
        springDataBase = (SpringDataBase) context.getBean("testDataBase");
        java.sql.Connection connection = null;
        try {
            connection = springDataBase.getConnection();
            Liquibase liquibase = null;
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            liquibase = new Liquibase("src\\main\\resources\\liquibase\\master.xml", new FileSystemResourceAccessor(), database);
            liquibase.update(new Contexts());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    protected void setUp() {
        springDataBase.execute("delete parameters");
        springDataBase.execute("delete dbobjects");
    }

    public void testParameterRowMapper(){
        Entity expected = new DBObject();
        expected.setId(BigInteger.valueOf(10500));
        springDataBase.execute("insert into dbobjects values(" + expected.getId() + ", " +
                expected.getObjectTypeId() + ", " +
                expected.getParentId() + ")");
        Entity actual = (Entity) springDataBase.executeObjectQuery(
                "select type.name, entity.object_id, entity.parent_id from dbobjects entity join object_types type " +
                        "on (entity.object_type_id = type.object_type_id) where entity.object_id = " + expected.getId(),
                new DBObjectRowMapper());
        Assert.assertEquals("entity id values", expected.getId(), actual.getId());
        Assert.assertEquals("entity object type values", expected.getObjectTypeId(), actual.getObjectTypeId());
        Assert.assertEquals("entity parent id values", expected.getParentId(), actual.getParentId());
    }
}