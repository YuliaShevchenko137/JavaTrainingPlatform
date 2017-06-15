package com.netcracker.lab3.jtp;

import com.netcracker.lab3.jtp.annotation.Attribute;
import com.netcracker.lab3.jtp.enums.AttributeType;
import com.netcracker.lab3.jtp.entity.Entity;
import com.netcracker.lab3.jtp.impl.EntityManagerImpl;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;

@NoArgsConstructor
@Slf4j
public class DataConverter {
    public Object dataConvert(Class fieldClass, String value){
        if (((Attribute)fieldClass.getAnnotation(Attribute.class)).value().equals(AttributeType.Object)) {
            ApplicationContext context = new ClassPathXmlApplicationContext("Beans/EntityManagerBeans.xml");
            EntityManagerImpl entityManager = (EntityManagerImpl)context.getBean("EntityManager");
            return entityManager.getObjectById(new BigInteger(value));
        } else if (((Attribute)fieldClass.getAnnotation(Attribute.class)).value().equals(AttributeType.Parameter)) {
            ApplicationContext context = new ClassPathXmlApplicationContext("Beans/EntityManagerBeans.xml");
            EntityManagerImpl entityManager = (EntityManagerImpl)context.getBean("EntityManager");
            return entityManager.getParameterById(new BigInteger(value));
        } else if(fieldClass.equals(String.class)) {
            return value;
        } else if(fieldClass.equals(BigDecimal.class)) {
            return new BigDecimal(value);
        } else if(fieldClass.equals(BigInteger.class)) {
            return new BigInteger(value);
        } else if(fieldClass.equals(Calendar.class)) {
            return calendarFromData(value);
        }
        return value;
    }

    public Calendar calendarFromData(String data){
        String[] strings = data.split(" ");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]),
                Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), Integer.parseInt(strings[5]));
        return calendar;
    }

    public String calendarToData(Calendar calendar){
        return "to_date('" + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.MONTH) + " " +
                calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + " " +
                calendar.get(Calendar.MINUTE)  + " " + calendar.get(Calendar.SECOND)  +
                "','yyyy mm dd hh24 mi ss')";
    }

    public String convertToData(Entity entity, Field field) {
        try {
            if (field.getAnnotation(Attribute.class).value().equals(AttributeType.Date)) {
                Calendar calendar = (Calendar) field.get(entity);
                return calendarToData(calendar);
            } else if (field.getAnnotation(Attribute.class).value().equals(AttributeType.Data) ||
                    field.getAnnotation(Attribute.class).value().equals(AttributeType.String) ||
                    field.getAnnotation(Attribute.class).value().equals(AttributeType.XML)) {
                return "'" + field.get(entity).toString() + "'";
            }
            else if (field.getAnnotation(Attribute.class).value().equals(AttributeType.Object)) {
                return ((Entity) field.get(entity)).getId().toString();
            } else if (field.getAnnotation(Attribute.class).value().equals(AttributeType.Parameter)) {
                return ((ParameterReference) field.get(entity)).getId().toString();
            }
        } catch (IllegalAccessException e) {
            log.error(e.getMessage());
        }
        String data = null; //use for AttributeType.Integer and AttributeType.Decimal too
        try {
            data = field.get(entity).toString();
        } catch (IllegalAccessException e) {
            log.error(e.getMessage());
        }
        return data;
    }

    public String convertToData(Object fieldValue) {
        if (fieldValue.getClass().equals(Calendar.class)) {
            return calendarToData((Calendar) fieldValue);
        } else if (fieldValue.getClass().equals(String.class)) {
            return "'" + fieldValue.toString() + "'";
        } else if (fieldValue.getClass().equals(BigDecimal.class) ||
                fieldValue.getClass().equals(BigInteger.class)) {
            return fieldValue.toString();
        } else if (fieldValue.getClass().equals(Entity.class)) {
            return ((Entity) fieldValue).getId().toString();
        } else if (fieldValue.getClass().equals(ParameterReferencesImpl.class)) {
            return ((ParameterReference) fieldValue).getId().toString();
        }
        return fieldValue.toString();
    }

}
