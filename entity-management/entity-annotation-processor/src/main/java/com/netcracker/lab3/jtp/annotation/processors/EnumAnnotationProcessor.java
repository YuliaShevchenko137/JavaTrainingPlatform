package com.netcracker.lab3.jtp.annotation.processors;

import com.google.auto.service.AutoService;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.IOException;

import com.netcracker.lab3.jtp.KeyGenerator;
import com.netcracker.lab3.jtp.annotation.*;
import com.netcracker.lab3.jtp.enums.AttributeType;
import com.netcracker.lab3.jtp.exceptions.EnumClassAnnotationException;
import com.netcracker.lab3.jtp.file.*;

import java.math.BigInteger;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.netcracker.lab3.jtp.annotation.processors.ParameterTypeAnnotationProcessor.*;
import static java.util.Objects.nonNull;

@SupportedAnnotationTypes({"com.netcracker.lab3.jtp.annotation.EnumType", "com.netcracker.lab3.jtp.annotation.FieldName",
        "com.netcracker.lab3.jtp.annotation.EnumClass"})
@AutoService(Processor.class)
public class EnumAnnotationProcessor extends AbstractProcessor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final static String PATH_ENUM_OBJECTS = "entity-management/entity-management-impl/src/main/resources/liquibase/changeLogs/enums.xml";
    private final static String SELECT_ATTRIBUTES = "entity-management/entity-management-impl/src/main/resources/liquibase/changeLogParts/sqlSelectAttribute.txt";
    private final static String LIQUIBASE_COLOMN_SQL = "entity-management/entity-management-impl/src/main/resources/liquibase/changeLogParts/changeLogColomnSql.txt";
    private String nameField;
    private String enumNameField;
    private int typeId;
    private TypeElement enumAnnotation;
    private String insert;
    private String column;
    private String sqlAttributes;
    private String columnSql;

    public void initializeWriters() {
        try {
            FileWriter.writeFile(PATH_ENUM_OBJECTS, String.format(FileReader.readFile(LIQUIBASE_TOP), "insert enum objects"));
            insert = FileReader.readFile(LIQUIBASE_INSERT);
            column = FileReader.readFile(LIQUIBASE_COLOMN);
            sqlAttributes = FileReader.readFile(SELECT_ATTRIBUTES);
            columnSql = FileReader.readFile(LIQUIBASE_COLOMN_SQL);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    @SuppressWarnings({"PMD.AvoidDeeplyNestedIfStmts"})
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if(!annotations.isEmpty()) {
            initializeWriters();
            for (TypeElement annotation : annotations) {
                choseDealMethod(annotation, roundEnv);
            }
            try {
                FileWriter.appendFile(PATH_ENUM_OBJECTS, FileReader.readFile(LIQUIBASE_BOTTOM));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return true;
    }

    public void choseDealMethod(TypeElement annotation, RoundEnvironment roundEnv){
        if(annotation.getSimpleName().contentEquals(EnumType.class.getSimpleName())) {
            enumAnnotation = annotation;
        } else if (annotation.getSimpleName().contentEquals(EnumClass.class.getSimpleName())){
            try {
                dealEnumClass(roundEnv.getElementsAnnotatedWith(annotation));
            } catch (EnumClassAnnotationException e) {
                log.error(e.getMessage());
            }
        } else if(annotation.getSimpleName().contentEquals(FieldName.class.getSimpleName())) {
            dealFieldName(roundEnv.getElementsAnnotatedWith(annotation));
        }
        if(typeId != 0 && nonNull(nameField) && nonNull(enumNameField) && nonNull(enumAnnotation)) {
            dealEnum(roundEnv.getElementsAnnotatedWith(enumAnnotation));
        }
    }

    public void dealEnumClass(Set<? extends Element> elements)throws EnumClassAnnotationException{
        if(elements.size() != 1)
            throw new EnumClassAnnotationException("Only one class must have EnumClass annotation");
        for (Element element : elements) {
            typeId = element.getAnnotation(DBObjectType.class).id();
        }
    }

    public void dealFieldName(Set<? extends Element> elements) {
        for (Element element : elements) {
            if(element.getAnnotation(FieldName.class).name().equals("name")) {
                nameField = element.getSimpleName().toString();
            }
            if(element.getAnnotation(FieldName.class).name().equals("enumName")) {
                enumNameField = element.getSimpleName().toString();
            }
        }
    }


    public void dealEnum(Set<? extends Element> elements){
        for (Element element : elements) {
            TypeElement anClass = (TypeElement) element;
            for (Element enumElement : anClass.getEnclosedElements()) {
                if (enumElement.getKind().equals(ElementKind.ENUM_CONSTANT)) {
                    try {
                       writeEnumObject(enumElement, element);
                    } catch (IOException ex) {
                        log.error(ex.getMessage());
                    }
                }
            }
        }
    }

    public void writeEnumObject(Element enumElement, Element classElement) throws IOException{
        BigInteger id =  KeyGenerator.generate();
        String columns =
                String.format(column,
                        "OBJECT_ID", id) +
                String.format(column,
                        "PARENT_ID", "null") +
                String.format(column,
                        "OBJECT_TYPE_ID", typeId).replace("\n", "");
        String insertObject = String.format(insert, "DBOBJECTS", columns);
        String enumName = String.format(column,
                "PARAMETER_ID", KeyGenerator.generate()) +
                String.format(column,
                        "object_id", id) +
                String.format(columnSql,
                        "attribute_id",
                        String.format(sqlAttributes, nameField, AttributeType.String.name()).replace("\n", "") ) +
                String.format(column,
                        AttributeType.String.name() + "_value", classElement.getSimpleName().toString()).replace("\n", "");
        String insertEnumName = String.format(insert, "PARAMETERS", enumName);
        String enumClass = String.format(column,
                "PARAMETER_ID", KeyGenerator.generate()) +
                String.format(column,
                        "object_id", id) +
                String.format(columnSql,
                        "attribute_id",
                        String.format(sqlAttributes, enumNameField, AttributeType.String.name()).replace("\n", "") ) +
                String.format(column,
                        AttributeType.String.name() + "_value", enumElement.getSimpleName().toString()).replace("\n", "");
        String insertEnumValue = String.format(insert, "DBOBJECTS", enumClass);
        FileWriter.appendFile(PATH_ENUM_OBJECTS, insertObject + "\n" + insertEnumName + "\n" + insertEnumValue);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}