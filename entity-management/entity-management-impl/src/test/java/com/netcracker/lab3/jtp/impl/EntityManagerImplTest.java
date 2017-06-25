package com.netcracker.lab3.jtp.impl;

import com.netcracker.lab3.jtp.db.SpringDataBase;
import com.netcracker.lab3.jtp.entity.Course;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.FileSystemResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.dbunit.*;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Calendar;

@Slf4j
@SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.SignatureDeclareThrowsException",
        "PMD.AvoidCatchingGenericException", "PMD.AvoidDuplicateLiterals"
, "PMD.FinalFieldCouldBeStatic"})
public class EntityManagerImplTest extends DBTestCase{

    protected IDatabaseTester tester;
    EntityManagerImpl entityManager;

    private final String[] parametersExpectedColumns = {"INTEGER_VALUE", "DECIMAL_VALUE", "STRING_VALUE", "XML_VALUE",
            "DATE_VALUE", "DATE_VALUE", "LIST_VALUE", "OBJECT_VALUE", "PARAMETER_VALUE"};
    private final String[] objectsExpectedColumns = {"OBJECT_TYPE_ID"};
    private final String parametersTable = "PARAMETERS";
    private final String[] ignoreParameters = {"PARAMETER_ID","OBJECT_ID","ATTRIBUTE_ID"};
    private final String objectsTable = "DBOBJECTS";
    private final String[] ignoreObjects = {"OBJECT_ID"};
    private final String[] testedTables = {"DBOBJECTS", "PARAMETERS"};

    public EntityManagerImplTest(String name) {
        super(name);
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "org.h2.Driver");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:h2:~/test");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "sa");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "");
    }

    @Override
    protected void setUp(){
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans/EntityManagerBeans.xml");
        tester = (IDatabaseTester) context.getBean("tester");
        SpringDataBase springDataBase = (SpringDataBase) context.getBean("testDataBase");
        entityManager = new EntityManagerImpl(springDataBase);
        java.sql.Connection connection = null;
//        springDataBase.execute("delete databasechangelog");
//        springDataBase.execute("delete databasechangeloglock");
        try {
            connection = entityManager.getConnection();
            Liquibase liquibase = null;
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            liquibase = new Liquibase("src\\main\\resources\\liquibase\\master.xml", new FileSystemResourceAccessor(), database);
            liquibase.update(new Contexts());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    public void testInsert(){
        Course course = new Course();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 11, 20, 0, 0, 0);
        course.setBeginDate((Calendar) calendar.clone());
        calendar.set(2016, 11, 25, 0, 0, 0);
        course.setEndDate((Calendar) calendar.clone());
        calendar.set(2016, 10, 15, 0, 0, 0);
        course.setCreationDate((Calendar) calendar.clone());
        course.setName("Data Base");
        course.setDescription("insert");
        course.setCreatorId(BigInteger.valueOf(200));

        entityManager.insert(course);
        try {
            IDataSet data = tester.getConnection().createDataSet(testedTables);
            IDataSet expected = new FlatXmlDataSetBuilder().build(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("DataSet/insertTest.xml"));
            ReplacementDataSet replacedDataSet = new ReplacementDataSet(expected);
            replacedDataSet.addReplacementObject("[null]", null);
            ITable sortedExpected = new SortedTable(replacedDataSet.getTable(objectsTable), objectsExpectedColumns);
            ITable sortedActual = new SortedTable(data.getTable(objectsTable), objectsExpectedColumns);
            Assertion.assertEqualsIgnoreCols(sortedExpected, sortedActual, ignoreObjects);
            sortedActual = new SortedTable(data.getTable(parametersTable), parametersExpectedColumns);
            sortedExpected = new SortedTable(replacedDataSet.getTable(parametersTable), parametersExpectedColumns);
            Assertion.assertEqualsIgnoreCols(sortedExpected, sortedActual, ignoreParameters);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            try {
                entityManager.getConnection().rollback();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void testUpdate(){
        Course course = new Course();
        course.setName("Data Base");
        entityManager.insert(course);
        course.setName("New database");
        entityManager.update(course);
        try {
            IDataSet data = tester.getConnection().createDataSet(testedTables);
            IDataSet expected = new FlatXmlDataSetBuilder().build(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("DataSet/updateTest.xml"));
            ReplacementDataSet replacedDataSet = new ReplacementDataSet(expected);
            replacedDataSet.addReplacementObject("[null]", null);

            ITable sortedExpected = new SortedTable(replacedDataSet.getTable(objectsTable), objectsExpectedColumns);
            ITable sortedActual = new SortedTable(data.getTable(objectsTable), objectsExpectedColumns);
            Assertion.assertEqualsIgnoreCols(sortedExpected, sortedActual, ignoreObjects);
            sortedActual = new SortedTable(data.getTable(parametersTable), parametersExpectedColumns);
            sortedExpected = new SortedTable(replacedDataSet.getTable(parametersTable), parametersExpectedColumns);
            Assertion.assertEqualsIgnoreCols(sortedExpected, sortedActual, ignoreParameters);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            try {
                entityManager.getConnection().rollback();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void testDelete(){
        Course course = new Course();
        course.setName("Java SE");
        entityManager.insert(course);
        try {
            IDataSet data = tester.getConnection().createDataSet(testedTables);
            IDataSet expected = new FlatXmlDataSetBuilder().build(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("DataSet/beforeDeleteTest.xml"));
            ReplacementDataSet replacedDataSet = new ReplacementDataSet(expected);
            replacedDataSet.addReplacementObject("[null]", null);
            ITable sortedExpected = new SortedTable(replacedDataSet.getTable(objectsTable), objectsExpectedColumns);
            ITable sortedActual = new SortedTable(data.getTable(objectsTable), objectsExpectedColumns);
            Assertion.assertEqualsIgnoreCols(sortedExpected, sortedActual, ignoreObjects);
            sortedActual = new SortedTable(data.getTable(parametersTable), parametersExpectedColumns);
            sortedExpected = new SortedTable(replacedDataSet.getTable(parametersTable), parametersExpectedColumns);
            Assertion.assertEqualsIgnoreCols(sortedExpected, sortedActual, ignoreParameters);

            entityManager.delete(entityManager.getObjectsByType(Course.class).get(0));
            data = tester.getConnection().createDataSet(testedTables);
            expected = new FlatXmlDataSetBuilder().build(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("DataSet/afterDeleteTest.xml"));
            replacedDataSet = new ReplacementDataSet(expected);
            replacedDataSet.addReplacementObject("[null]", null);
            sortedExpected = new SortedTable(replacedDataSet.getTable(objectsTable), objectsExpectedColumns);
            sortedActual = new SortedTable(data.getTable(objectsTable), objectsExpectedColumns);
            Assertion.assertEqualsIgnoreCols(sortedExpected, sortedActual, ignoreObjects);
            sortedActual = new SortedTable(data.getTable(parametersTable), parametersExpectedColumns);
            sortedExpected = new SortedTable(replacedDataSet.getTable(parametersTable), parametersExpectedColumns);
            Assertion.assertEqualsIgnoreCols(sortedExpected, sortedActual, ignoreParameters);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            try {
                entityManager.getConnection().rollback();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void testGetObjectById(){
        Course course = new Course();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 10, 20, 0, 0, 0);
        course.setBeginDate((Calendar) calendar.clone());
        calendar.set(2016, 9, 25, 0, 0, 0);
        course.setEndDate((Calendar) calendar.clone());
        calendar.set(2016, 10, 15, 0, 0, 0);
        course.setCreationDate((Calendar) calendar.clone());
        course.setName("Data Base");
        course.setDescription("insert");
        course.setCreatorId(BigInteger.valueOf(200));
        entityManager.insert(course);
        Course courseReaded = (Course) entityManager.getObjectById(course.getId());
        Assert.assertEquals("Equals id", course.getId(), courseReaded.getId());
        Assert.assertEquals("Equals parent id", course.getParentId(), courseReaded.getParentId());

        Assert.assertEquals("Equals names", course.getName(), courseReaded.getName());
        Assert.assertEquals("Equals description", course.getDescription(), courseReaded.getDescription());
        Assert.assertEquals("Equals creator id", course.getCreatorId(), courseReaded.getCreatorId());

        String expectation = course.getBeginDate().get(Calendar.YEAR) + " " + course.getBeginDate().get(Calendar.MONTH) + " " +
                course.getBeginDate().get(Calendar.DAY_OF_MONTH) + " " + course.getBeginDate().get(Calendar.HOUR_OF_DAY) + " " +
                course.getBeginDate().get(Calendar.MINUTE)  + " " + course.getBeginDate().get(Calendar.SECOND);
        String actual = courseReaded.getBeginDate().get(Calendar.YEAR) + " " + courseReaded.getBeginDate().get(Calendar.MONTH) + " " +
                courseReaded.getBeginDate().get(Calendar.DAY_OF_MONTH) + " " + courseReaded.getBeginDate().get(Calendar.HOUR_OF_DAY) + " " +
                calendar.get(Calendar.MINUTE)  + " " + calendar.get(Calendar.SECOND);
        Assert.assertEquals("Equals begin date", expectation, actual);
        expectation = course.getEndDate().get(Calendar.YEAR) + " " + course.getEndDate().get(Calendar.MONTH) + " " +
                course.getEndDate().get(Calendar.DAY_OF_MONTH) + " " + course.getEndDate().get(Calendar.HOUR_OF_DAY) + " " +
                course.getEndDate().get(Calendar.MINUTE)  + " " + course.getEndDate().get(Calendar.SECOND);
        actual = courseReaded.getEndDate().get(Calendar.YEAR) + " " + courseReaded.getEndDate().get(Calendar.MONTH) + " " +
                courseReaded.getEndDate().get(Calendar.DAY_OF_MONTH) + " " + courseReaded.getEndDate().get(Calendar.HOUR_OF_DAY) + " " +
                calendar.get(Calendar.MINUTE)  + " " + calendar.get(Calendar.SECOND);
        Assert.assertEquals("Equals end date", expectation, actual);
        expectation = course.getCreationDate().get(Calendar.YEAR) + " " + course.getCreationDate().get(Calendar.MONTH) + " " +
                course.getCreationDate().get(Calendar.DAY_OF_MONTH) + " " + course.getCreationDate().get(Calendar.HOUR_OF_DAY) + " " +
                course.getCreationDate().get(Calendar.MINUTE)  + " " + course.getCreationDate().get(Calendar.SECOND);
        actual = courseReaded.getCreationDate().get(Calendar.YEAR) + " " + courseReaded.getCreationDate().get(Calendar.MONTH) + " " +
                courseReaded.getCreationDate().get(Calendar.DAY_OF_MONTH) + " " + courseReaded.getCreationDate().get(Calendar.HOUR_OF_DAY) + " " +
                calendar.get(Calendar.MINUTE)  + " " + calendar.get(Calendar.SECOND);
        Assert.assertEquals("Equals creation date", expectation, actual);
        Assert.assertNull(courseReaded.getUserList());
        Assert.assertNull(courseReaded.getState());
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return tester.getDataSet();
    }
}
