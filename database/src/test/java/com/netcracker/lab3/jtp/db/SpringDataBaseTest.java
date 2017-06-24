package com.netcracker.lab3.jtp.db;

import liquibase.Liquibase;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.FileSystemResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.dbunit.*;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.sql.SQLException;

@Slf4j
@SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.AvoidCatchingGenericException"})
public class SpringDataBaseTest extends DBTestCase {
    private SpringDataBase dataBase;
    private IDatabaseTester tester;

    public SpringDataBaseTest(String name) {
        super(name);
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "org.h2.Driver");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:h2:~/test");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "sa");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "");
    }

    @Override
    public void setUp() {
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans/DataBaseBeans.xml");
        tester = (IDatabaseTester) context.getBean("tester");
        dataBase = (SpringDataBase) context.getBean("testDataBase");
        java.sql.Connection connection = null;
        try {
            connection = dataBase.getConnection();
            Liquibase liquibase = null;
            DatabaseChangeLog changeLog = new DatabaseChangeLog();
            changeLog.setPhysicalFilePath("\\database\\src\\main\\resources\\liquibase\\master.xml");
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            liquibase = new Liquibase(changeLog, new FileSystemResourceAccessor(), database);
            liquibase.update("Test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  public void testDMLStatment() {
        try {
            dataBase.execute("create table a ( b number(5) )");
            dataBase.execute("insert into a values(5)");
            IDataSet data = tester.getConnection().createDataSet(new String[]{"a"});
            IDataSet expected = new FlatXmlDataSetBuilder().build(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("DataSet/insertDataSet.xml"));
            Assertion.assertEquals(expected, data);

            dataBase.execute("update a  set b = 6");
            data = tester.getConnection().createDataSet(new String[]{"a"});
            expected = new FlatXmlDataSetBuilder().build(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("DataSet/updateDataSet.xml"));
            Assertion.assertEquals(expected, data);

            dataBase.execute("insert into a values(4)");
            data = tester.getConnection().createDataSet(new String[]{"a"});
            expected = new FlatXmlDataSetBuilder().build(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("DataSet/beforeDeleteDataSet.xml"));
            Assertion.assertEquals(expected, data);

            dataBase.execute("delete a where b = 6");
            data = tester.getConnection().createDataSet(new String[]{"a"});
            expected = new FlatXmlDataSetBuilder().build(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("DataSet/afterDeleteDataSet.xml"));
            Assertion.assertEquals(expected, data);
        } catch (DataSetException | SQLException e) {
            log.error(e.getMessage());
        } catch (DatabaseUnitException e) {
            log.debug(e.getMessage());
        } catch (Exception e) {
            log.debug(e.getMessage());
        } finally {
            dataBase.execute("drop table a");
        }
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return tester.getDataSet();
    }

}
