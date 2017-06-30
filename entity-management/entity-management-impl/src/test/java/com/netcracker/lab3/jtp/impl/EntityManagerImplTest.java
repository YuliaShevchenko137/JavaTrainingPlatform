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
import java.util.Calendar;

import static org.dbunit.PropertiesBasedJdbcDatabaseTester.*;

@Slf4j
@SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.SignatureDeclareThrowsException",
        "PMD.AvoidCatchingGenericException", "PMD.AvoidDuplicateLiterals"
, "PMD.FinalFieldCouldBeStatic"})
public class EntityManagerImplTest extends DBTestCase{

    private final IDatabaseTester tester;
    private final EntityManagerImpl entityManager;

    private static final String[] PARAMETERS_EXPECTED_COLUMNS = {"INTEGER_VALUE", "DECIMAL_VALUE", "STRING_VALUE", "XML_VALUE",
            "DATE_VALUE", "DATE_VALUE", "LIST_VALUE", "OBJECT_VALUE", "PARAMETER_VALUE"};
    private static final String[] OBJECTS_EXPECTED_COLUMNS = {"OBJECT_TYPE_ID"};
    private static final String PARAMETERS_TABLE = "PARAMETERS";
    private static final String[] IGNORE_PARAMETERS = {"PARAMETER_ID","OBJECT_ID","ATTRIBUTE_ID"};
    private static final String OBJECTS_TABLE = "DBOBJECTS";
    private static final String[] IGNORE_OBJECTS = {"OBJECT_ID"};
    private static final String[] TESTED_TABLES = {"DBOBJECTS", "PARAMETERS"};

    public EntityManagerImplTest(String name) {
        super(name);
        System.setProperty(DBUNIT_DRIVER_CLASS, "org.h2.Driver");
        System.setProperty(DBUNIT_CONNECTION_URL, "jdbc:h2:~/test");
        System.setProperty(DBUNIT_USERNAME, "sa");
        System.setProperty(DBUNIT_PASSWORD, "");
//        use after changing changesets(after first build)
//        springDataBase.execute("delete databasechangelog");
//        springDataBase.execute("delete databasechangeloglock");

        ApplicationContext context = new ClassPathXmlApplicationContext("Beans/EntityManagerBeans.xml");
        tester = (IDatabaseTester) context.getBean("tester");
        SpringDataBase springDataBase = (SpringDataBase) context.getBean("testDataBase");
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
            IDataSet data = tester.getConnection().createDataSet(TESTED_TABLES);
            IDataSet expected = new FlatXmlDataSetBuilder().build(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("DataSet/insertTest.xml"));
            ReplacementDataSet replacedDataSet = new ReplacementDataSet(expected);
            replacedDataSet.addReplacementObject("[null]", null);
            ITable sortedExpected = new SortedTable(replacedDataSet.getTable(OBJECTS_TABLE), OBJECTS_EXPECTED_COLUMNS);
            ITable sortedActual = new SortedTable(data.getTable(OBJECTS_TABLE), OBJECTS_EXPECTED_COLUMNS);
            Assertion.assertEqualsIgnoreCols(sortedExpected, sortedActual, IGNORE_OBJECTS);
            sortedActual = new SortedTable(data.getTable(PARAMETERS_TABLE), PARAMETERS_EXPECTED_COLUMNS);
            sortedExpected = new SortedTable(replacedDataSet.getTable(PARAMETERS_TABLE), PARAMETERS_EXPECTED_COLUMNS);
            Assertion.assertEqualsIgnoreCols(sortedExpected, sortedActual, IGNORE_PARAMETERS);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void testUpdate(){
        Course course = new Course();
        course.setName("Data Base");
        entityManager.insert(course);
        course.setName("New database");
        entityManager.update(course);
        try {
            IDataSet data = tester.getConnection().createDataSet(TESTED_TABLES);
            IDataSet expected = new FlatXmlDataSetBuilder().build(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("DataSet/updateTest.xml"));
            ReplacementDataSet replacedDataSet = new ReplacementDataSet(expected);
            replacedDataSet.addReplacementObject("[null]", null);

            ITable sortedExpected = new SortedTable(replacedDataSet.getTable(OBJECTS_TABLE), OBJECTS_EXPECTED_COLUMNS);
            ITable sortedActual = new SortedTable(data.getTable(OBJECTS_TABLE), OBJECTS_EXPECTED_COLUMNS);
            Assertion.assertEqualsIgnoreCols(sortedExpected, sortedActual, IGNORE_OBJECTS);
            sortedActual = new SortedTable(data.getTable(PARAMETERS_TABLE), PARAMETERS_EXPECTED_COLUMNS);
            sortedExpected = new SortedTable(replacedDataSet.getTable(PARAMETERS_TABLE), PARAMETERS_EXPECTED_COLUMNS);
            Assertion.assertEqualsIgnoreCols(sortedExpected, sortedActual, IGNORE_PARAMETERS);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void testDelete(){
        Course course = new Course();
        course.setName("Java SE");
        entityManager.insert(course);
        try {
            IDataSet data = tester.getConnection().createDataSet(TESTED_TABLES);
            IDataSet expected = new FlatXmlDataSetBuilder().build(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("DataSet/beforeDeleteTest.xml"));
            ReplacementDataSet replacedDataSet = new ReplacementDataSet(expected);
            replacedDataSet.addReplacementObject("[null]", null);
            ITable sortedExpected = new SortedTable(replacedDataSet.getTable(OBJECTS_TABLE), OBJECTS_EXPECTED_COLUMNS);
            ITable sortedActual = new SortedTable(data.getTable(OBJECTS_TABLE), OBJECTS_EXPECTED_COLUMNS);
            Assertion.assertEqualsIgnoreCols(sortedExpected, sortedActual, IGNORE_OBJECTS);
            sortedActual = new SortedTable(data.getTable(PARAMETERS_TABLE), PARAMETERS_EXPECTED_COLUMNS);
            sortedExpected = new SortedTable(replacedDataSet.getTable(PARAMETERS_TABLE), PARAMETERS_EXPECTED_COLUMNS);
            Assertion.assertEqualsIgnoreCols(sortedExpected, sortedActual, IGNORE_PARAMETERS);

            entityManager.delete(entityManager.getObjectsByType(Course.class).get(0));
            data = tester.getConnection().createDataSet(TESTED_TABLES);
            expected = new FlatXmlDataSetBuilder().build(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("DataSet/afterDeleteTest.xml"));
            replacedDataSet = new ReplacementDataSet(expected);
            replacedDataSet.addReplacementObject("[null]", null);
            sortedExpected = new SortedTable(replacedDataSet.getTable(OBJECTS_TABLE), OBJECTS_EXPECTED_COLUMNS);
            sortedActual = new SortedTable(data.getTable(OBJECTS_TABLE), OBJECTS_EXPECTED_COLUMNS);
            Assertion.assertEqualsIgnoreCols(sortedExpected, sortedActual, IGNORE_OBJECTS);
            sortedActual = new SortedTable(data.getTable(PARAMETERS_TABLE), PARAMETERS_EXPECTED_COLUMNS);
            sortedExpected = new SortedTable(replacedDataSet.getTable(PARAMETERS_TABLE), PARAMETERS_EXPECTED_COLUMNS);
            Assertion.assertEqualsIgnoreCols(sortedExpected, sortedActual, IGNORE_PARAMETERS);
        } catch (Exception e) {
            log.error(e.getMessage());
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