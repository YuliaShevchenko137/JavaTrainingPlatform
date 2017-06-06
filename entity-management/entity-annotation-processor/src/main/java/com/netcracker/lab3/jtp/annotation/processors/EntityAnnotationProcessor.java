package com.netcracker.lab3.jtp.annotation.processors;

import com.google.auto.service.AutoService;
import com.netcracker.lab3.jtp.annotation.Attribute;
import com.netcracker.lab3.jtp.annotation.DBObjectType;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import com.netcracker.lab3.jtp.db.KeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@SupportedAnnotationTypes({"com.netcracker.lab3.jtp.annotation.DBObjectType"})
@AutoService(Processor.class)
public class EntityAnnotationProcessor extends AbstractProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private BufferedWriter typeWriter;
    private BufferedWriter attributeWriter;

    public EntityAnnotationProcessor(){
        super();
        try {
            String objectTypesSQLPath = "database/src/main/resources/DBSQLFiles/objectTypes.sql";
            typeWriter = new BufferedWriter(new FileWriter(objectTypesSQLPath, false));
            typeWriter.write("--liquibase formatted sql\n\n" +
                    "--changeset javal3:6\n\n");
            typeWriter.flush();

            String attributesSQLPath = "database/src/main/resources/DBSQLFiles/attributes.sql";
            attributeWriter = new BufferedWriter(new FileWriter(attributesSQLPath, false));
            attributeWriter.write("--liquibase formatted sql\n\n" +
                    "--changeset javal3:7\n\n");
            attributeWriter.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<DBAttribute> attributes = new HashSet<>();
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            if (annotation.getSimpleName().contentEquals(DBObjectType.class.getSimpleName())) {
                for (Element element : annotatedElements) {
                    try {
                        TypeElement anClass = (TypeElement) element;
                        DBObjectType objectType = element.getAnnotation(DBObjectType.class);
                        DeclaredType declared = (DeclaredType)anClass.getSuperclass();
                        Element supertypeElement = declared.asElement();
                        DBObjectType parent = supertypeElement.getAnnotation(DBObjectType.class);
                        typeWriter.write("insert into OBJECT_TYPES values(" + objectType.id() + ",'" +
                                element.getSimpleName() + "',"
                                + (isNull(parent) ? null : parent.id()) + ");\n");
                        typeWriter.flush();
                        List<? extends Element> list =  anClass.getEnclosedElements();
                        for (int i = 0; i < list.size(); i++) {
                            if(list.get(i).getAnnotation(Attribute.class) !=  null) {
                                DBAttribute attribute = new DBAttribute();
                                attribute.setId(KeyGenerator.generate());
                                attribute.setName(list.get(i).getSimpleName().toString());
                                Attribute attrType = list.get(i).getAnnotation(Attribute.class);
                                attribute.setType(attrType.value().name());
                                attributes.add(attribute);
                            }
                        }
                    }catch(IOException ex){
                        log.error(ex.getMessage());
                    }
                }
            }
        }
        attributesGenerate(attributes);
        return true;
    }

    public void attributesGenerate(Set<DBAttribute> attributes){
        for (DBAttribute attribute: attributes) {
            try {
                attributeWriter.write("insert into ATTRIBUTES values(" + attribute.getId() + ",'" +
                        attribute.getName() + "','" + attribute.getType() + "');\n");
                attributeWriter.flush();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}
