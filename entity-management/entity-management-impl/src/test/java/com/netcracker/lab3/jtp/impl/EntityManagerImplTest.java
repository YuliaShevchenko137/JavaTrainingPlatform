package com.netcracker.lab3.jtp.impl;

import com.netcracker.lab3.jtp.db.SpringDataBase;
import com.netcracker.lab3.jtp.entity.Course;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.FileSystemResourceAccessor;
import org.dbunit.*;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.SQLException;
import java.util.Calendar;

@SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.SignatureDeclareThrowsException", "PMD.AvoidCatchingGenericException", "PMD.AvoidDuplicateLiterals"})
public class EntityManagerImplTest extends DBTestCase{

    protected IDatabaseTester tester;
    EntityManagerImpl entityManager;

    public EntityManagerImplTest(String name) {
        super(name);
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "org.h2.Driver");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:h2:mem:testdb");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "sa");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "");
    }

    @Override
    protected void setUp(){
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans/EntityManagerBeans.xml");
        tester = (IDatabaseTester) context.getBean("tester");
        SpringDataBase springDataBase = (SpringDataBase) context.getBean("testDataBase");
        entityManager = new EntityManagerImpl(springDataBase);

        String contextName = "Test";
        java.sql.Connection connection = null;
        try {
            connection = springDataBase.getConnection();
            Liquibase liquibase = null;
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            liquibase = new Liquibase(this.getClass().getResource("liquibase/changeLogs/initializeDB.xml").getPath(), new FileSystemResourceAccessor(), database);
            liquibase.update(contextName);
            liquibase = new Liquibase(this.getClass().getResource("liquibase/changeLogs/parameterTypes.xml").getPath(), new FileSystemResourceAccessor(), database);
            liquibase.update(contextName);
            liquibase = new Liquibase(this.getClass().getResource("liquibase/changeLogs/objectTypes.xml").getPath(), new FileSystemResourceAccessor(), database);
            liquibase.update(contextName);
            liquibase = new Liquibase(this.getClass().getResource("liquibase/changeLogs/attributes.xml").getPath(), new FileSystemResourceAccessor(), database);
            liquibase.update(contextName);
            liquibase = new Liquibase(this.getClass().getResource("liquibase/changeLogs/objectTypeAttributes.xml").getPath(), new FileSystemResourceAccessor(), database);
            liquibase.update(contextName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testInsert(){
        Course course = new Course();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 11, 20);
        course.setBeginDate((Calendar) calendar.clone());
        calendar.set(2016, 11, 25);
        course.setEndDate((Calendar) calendar.clone());
        calendar.set(2016, 10, 15);
        course.setCreationDate((Calendar) calendar.clone());
        course.setName("Data Base");
        entityManager.insert(course);
        try {
            IDataSet data = tester.getConnection().createDataSet(new String[]{"objects", "params"});
            IDataSet expected = new FlatXmlDataSetBuilder().build(Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream("DataSet/insertTest.xml"));
            String objectsTable = "objects";
            String[] ignoreObjects = {"object_id"};
            Assertion.assertEqualsIgnoreCols(expected.getTable(objectsTable), data.getTable(objectsTable), ignoreObjects);
            String paramsTable = "params";
            String[] ignoreParams = {"param_id","object_id","attribute_id"};
            Assertion.assertEqualsIgnoreCols(expected.getTable(paramsTable), data.getTable(paramsTable), ignoreParams);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                entityManager.getConnection().rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void testUpdate(){
        Course course = new Course();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 11, 20);
        course.setBeginDate((Calendar) calendar.clone());
        calendar.set(2015, 11, 25);
        course.setEndDate((Calendar) calendar.clone());
        calendar.set(2015, 10, 15);
        course.setCreationDate((Calendar) calendar.clone());
        course.setName("Data Base");
        entityManager.insert(course);
        calendar.set(2015, 11, 21);
        course.setBeginDate((Calendar) calendar.clone());
        entityManager.update(course);
        try {
            IDataSet data = tester.getConnection().createDataSet(new String[]{"objects", "params"});
            IDataSet expected = new FlatXmlDataSetBuilder().build(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("DataSet/updateTest.xml"));
            String objectsTable = "objects";
            String[] ignoreObjects = {"object_id"};
            Assertion.assertEqualsIgnoreCols(expected.getTable(objectsTable), data.getTable(objectsTable), ignoreObjects);
            String paramsTable = "params";
            String[] ignoreParams = {"param_id","object_id","attribute_id"};
            Assertion.assertEqualsIgnoreCols(expected.getTable(paramsTable), data.getTable(paramsTable), ignoreParams);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                entityManager.getConnection().rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void testDelete(){
        Calendar calendar = Calendar.getInstance();
        Course course = new Course();
        calendar.set(2015, 11, 13);
        course.setBeginDate((Calendar) calendar.clone());
        calendar.set(2015, 11, 16);
        course.setEndDate((Calendar) calendar.clone());
        calendar.set(2015, 10, 18);
        course.setCreationDate((Calendar) calendar.clone());
        course.setName("Java");
        entityManager.insert(course);
        try {
            IDataSet data = tester.getConnection().createDataSet(new String[]{"objects", "params"});
            IDataSet expected = new FlatXmlDataSetBuilder().build(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("DataSet/beforeDeleteTest.xml"));
            String objectsTable = "objects";
            String[] ignoreObjects = {"object_id"};
            Assertion.assertEqualsIgnoreCols(expected.getTable(objectsTable), data.getTable(objectsTable), ignoreObjects);
            String paramsTable = "params";
            String[] ignoreParams = {"param_id","object_id","attribute_id"};
            Assertion.assertEqualsIgnoreCols(expected.getTable(paramsTable), data.getTable(paramsTable), ignoreParams);

            entityManager.delete(entityManager.getObjectsByType(Course.class).get(0));
            data = tester.getConnection().createDataSet(new String[]{"objects", "params"});
            expected = new FlatXmlDataSetBuilder().build(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("DataSet/afterDeleteTest.xml"));
            Assertion.assertEqualsIgnoreCols(expected.getTable(objectsTable), data.getTable(objectsTable), ignoreObjects);
            Assertion.assertEqualsIgnoreCols(expected.getTable(paramsTable), data.getTable(paramsTable), ignoreParams);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                entityManager.getConnection().rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return tester.getDataSet();
    }
}
