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

import static java.util.Objects.isNull;

@NoArgsConstructor
@Slf4j
@SuppressWarnings("PMD.CyclomaticComplexity")
public class DataConverter {
    public static Object dataConvert(Field field, String value){
        if (AttributeType.Object.equals(field.getAnnotation(Attribute.class).value())) {
            return getEntityManager().getObjectById(new BigInteger(value));
        } else if (AttributeType.Parameter.equals(field.getAnnotation(Attribute.class).value())) {
            return getEntityManager().getParameterReference(value);
        } else if(field.getType().equals(String.class)) {
            return value;
        } else if(field.getType().equals(BigDecimal.class)) {
            return new BigDecimal(value);
        } else if(field.getType().equals(BigInteger.class)) {
            return new BigInteger(value);
        } else if(field.getType().equals(Calendar.class)) {
            return calendarFromData(value);
        }
        return value;
    }

    public static EntityManagerImpl getEntityManager(){
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans/EntityManagerBeans.xml");
        return  (EntityManagerImpl)context.getBean("EntityManager");
    }

    public static Object dataConvert(Class fieldClass, String value){
        if(fieldClass.equals(String.class)) {
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

    public static Calendar calendarFromData(String data){
        String[] strings = data.split(" ");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]),
                Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), Integer.parseInt(strings[5]));
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static String calendarToData(Calendar calendar){
        return "to_date('" + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.MONTH) + " " +
                calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + " " +
                calendar.get(Calendar.MINUTE)  + " " + calendar.get(Calendar.SECOND)  +
                "','yyyy mm dd hh24 mi ss')";
    }



    public static String convertToData(Entity entity, Field field) {
        field.setAccessible(true);
        try {
            if(isNull(field.get(entity))) {
                return "null";
            }
            if (field.getAnnotation(Attribute.class).value().equals(AttributeType.Date)) {
                Calendar calendar = (Calendar) field.get(entity);
                return calendarToData(calendar);
            }  else if ( field.getAnnotation(Attribute.class).value().equals(AttributeType.String) ||
                    field.getAnnotation(Attribute.class).value().equals(AttributeType.XML)) {
                return "'" + field.get(entity).toString() + "'";
            }
            else if (field.getAnnotation(Attribute.class).value().equals(AttributeType.Object)) {
                return ((Entity) field.get(entity)).getId().toString();
            } else if (field.getAnnotation(Attribute.class).value().equals(AttributeType.Parameter)) {
                return ((ParametrReference) field.get(entity)).getObjectId().toString();
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

    public static String convertToData(Object fieldValue) {
        if (fieldValue.getClass().equals(Calendar.class)) {
            return calendarToData((Calendar) fieldValue);
        } else if (fieldValue.getClass().equals(String.class)) {
            return "'" + fieldValue.toString() + "'";
        } else if (fieldValue.getClass().equals(BigDecimal.class) ||
                fieldValue.getClass().equals(BigInteger.class)) {
            return fieldValue.toString();
        } else if (fieldValue.getClass().equals(Entity.class)) {
            return ((Entity) fieldValue).getId().toString();
        } else if (fieldValue.getClass().equals(ParametrReferencesImpl.class)) {
            return ((ParametrReference) fieldValue).getObjectId().toString();
        }
        return fieldValue.toString();
    }
//
//    public static String convertToData(String fieldValue) {
//        return "'" + fieldValue + "'";
//    }
//
//    public static String convertToData(Calendar fieldValue) {
//        return calendarToData((Calendar) fieldValue);
//    }
//
//    public static String convertToData(ParametrReferencesImpl fieldValue) {
//        return ((ParametrReference) fieldValue).getObjectId().toString();
//    }
//
//    public static String convertToData(Entity fieldValue) {
//        return ((Entity) fieldValue).getId().toString();
//    }
//
//    public static String convertToData(BigDecimal fieldValue) {
//        return fieldValue.toString();
//    }
//
//    public static String convertToData(BigInteger fieldValue) {
//        return fieldValue.toString();
//    }
}
