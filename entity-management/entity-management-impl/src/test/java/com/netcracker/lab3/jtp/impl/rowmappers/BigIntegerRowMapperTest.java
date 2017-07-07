package com.netcracker.lab3.jtp.impl.rowmappers;

import com.netcracker.lab3.jtp.db.SpringDataBase;
import junit.framework.TestCase;
import org.junit.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigInteger;

public class BigIntegerRowMapperTest extends TestCase{
    private final SpringDataBase springDataBase;

    public BigIntegerRowMapperTest(){
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans/EntityManagerBeans.xml");
        springDataBase = (SpringDataBase) context.getBean("testDataBase");
    }

    public void testMapper(){
        springDataBase.execute("create table a(b number(38))");
        springDataBase.execute("insert into a values(32)");
        BigInteger actual = (BigInteger) springDataBase.executeObjectQuery("select * from a", new BigIntegerRowMapper());
        BigInteger expected = BigInteger.valueOf(32);
        Assert.assertEquals("test BigInteger row mapper", expected, actual);
        springDataBase.execute("drop table a");
    }

}
