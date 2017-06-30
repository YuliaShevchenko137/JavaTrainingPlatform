package com.netcracker.lab3.jtp.rowmappers;


import com.netcracker.lab3.jtp.ParametrImpl;
import com.netcracker.lab3.jtp.db.SpringDataBase;
import com.netcracker.lab3.jtp.entity.DBObject;
import com.netcracker.lab3.jtp.impl.EntityManagerImpl;
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

@Slf4j
@SuppressWarnings({"PMD.AvoidCatchingGenericException"})
public class ParameterRowMapperTest extends TestCase {
    private final EntityManagerImpl entityManager;
    private final SpringDataBase springDataBase;

    public ParameterRowMapperTest(){
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans/EntityManagerBeans.xml");
        springDataBase = (SpringDataBase) context.getBean("testDataBase");
        entityManager = new EntityManagerImpl(springDataBase);
        java.sql.Connection connection = null;
        try {
            connection = entityManager.getConnection();
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
        entityManager.execute("delete parameters");
        entityManager.execute("delete dbobjects");
    }

    public void testParameterRowMapper(){
        String parameterName = "description";
        String parameterValue = "test";
        DBObject object = new DBObject();
        object.setDescription(parameterValue);
        entityManager.insert(object);
        ParametrImpl expected = new ParametrImpl();
        expected.setName(parameterName);
        expected.setValue(parameterValue);
        ParametrImpl actual = (ParametrImpl) springDataBase.executeObjectQuery("select attr.name, STRING_VALUE" +
                " from attributes attr join PARAMETERS params " +
                "on (attr.ATTRIBUTE_ID = params.ATTRIBUTE_ID) where params.object_id = " + object.getId() +
                " and STRING_VALUE is not null",
                new ParameterRowMapper());
        Assert.assertEquals("parameter name equals", expected.getName(), actual.getName());
        Assert.assertEquals("parameter value equals", expected.getValue(), actual.getValue());
    }
}
