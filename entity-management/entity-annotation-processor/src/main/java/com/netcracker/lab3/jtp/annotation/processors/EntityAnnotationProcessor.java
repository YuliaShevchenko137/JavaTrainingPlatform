package com.netcracker.lab3.jtp.annotation.processors;

import com.google.auto.service.AutoService;
import com.netcracker.lab3.jtp.annotation.Attribute;
import com.netcracker.lab3.jtp.annotation.DBObjectType;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import com.netcracker.lab3.jtp.KeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@SupportedAnnotationTypes({"com.netcracker.lab3.jtp.annotation.DBObjectType"})
@AutoService(Processor.class)
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.CyclomaticComplexity"})
public class EntityAnnotationProcessor extends AbstractProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private BufferedWriter typeWriter;
    private BufferedWriter attributeWriter;
    private BufferedWriter objectAttributeWriter;

    Set<DBAttribute> attributes = new HashSet<>();
    List<ObjectTypeAttribute> objectAttributes = new ArrayList<>();

    public void initializeWriters(){
        try {
            Path objectTypesXMLPath = Paths.get("database/src/main/resources/liquibase/changeLogs/objectTypes.xml");
            typeWriter = Files.newBufferedWriter(objectTypesXMLPath, Charset.forName("UTF-8"));
            typeWriter.write("<databaseChangeLog\n" +
                    "\t\txmlns=\"http://www.liquibase.org/xml/ns/dbchangelog/1.8\"\n" +
                    "\t\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "\t\txsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog/1.8\n" +
                    "\t\thttp://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-1.8.xsd\">\n" +
                    "\n" +
                    "\t<property name=\"author\" value=\"javal3\"/>\n" +
                    "\t<changeSet author=\"${author}\" id=\"insert into OBJECT_TYPES\">\n" +
                    "\t\t<validCheckSum>ANY</validCheckSum>\n");
            typeWriter.flush();

            Path attributesXMLPath = Paths.get("database/src/main/resources/liquibase/changeLogs/attributes.xml");
            attributeWriter = Files.newBufferedWriter(attributesXMLPath, Charset.forName("UTF-8"));
            attributeWriter.write("<databaseChangeLog\n" +
                    "\t\txmlns=\"http://www.liquibase.org/xml/ns/dbchangelog/1.8\"\n" +
                    "\t\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "\t\txsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog/1.8\n" +
                    "\t\thttp://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-1.8.xsd\">\n" +
                    "\n" +
                    "\t<property name=\"author\" value=\"javal3\"/>\n" +
                    "\t<changeSet author=\"${author}\" id=\"insert into ATTRIBUTES\">\n" +
                    "\t\t<validCheckSum>ANY</validCheckSum>\n");
            attributeWriter.flush();

            Path objectAttributeXMLPath = Paths.get("database/src/main/resources/liquibase/changeLogs/objectTypeAttributes.xml");
            objectAttributeWriter = Files.newBufferedWriter(objectAttributeXMLPath, Charset.forName("UTF-8"));
            objectAttributeWriter.write("<databaseChangeLog\n" +
                    "\t\txmlns=\"http://www.liquibase.org/xml/ns/dbchangelog/1.8\"\n" +
                    "\t\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "\t\txsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog/1.8\n" +
                    "\t\thttp://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-1.8.xsd\">\n" +
                    "\n" +
                    "\t<property name=\"author\" value=\"javal3\"/>\n" +
                    "\t<changeSet author=\"${author}\" id=\"insert into OBJECT_TYPE_ATTRIBUTES\">\n" +
                    "\t\t<validCheckSum>ANY</validCheckSum>\n");
            objectAttributeWriter.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if(!annotations.isEmpty()) {
            initializeWriters();
            ArrayList<DBObject> withOutParent = new ArrayList<>();
            ArrayList<DBObject> withParent = new ArrayList<>();
            if (annotations != null) {
                for (TypeElement annotation : annotations) {
                    if (annotation.getSimpleName().contentEquals(DBObjectType.class.getSimpleName())) {
                        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
                        for (Element element : annotatedElements) {
                            DBObject object = new DBObject(element);
                            if (isNull(object.getParentId()) || containId(withParent, object)) {
                                dealDBObject(object);
                                withParent.add(object);
                                for (int i = 0; i < withOutParent.size(); i++) {
                                    if(containId(withParent, withOutParent.get(i))) {
                                        dealDBObject(withOutParent.get(i));
                                        withParent.add(withOutParent.get(i));
                                        withOutParent.remove(i);
                                    }
                                }
                            } else {
                                withOutParent.add(object);
                            }

                        }
                    }
                }
                try {
                    typeWriter.write("\t</changeSet>\n" +
                            "</databaseChangeLog>");
                    typeWriter.flush();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            attributesGenerate();
            objectAttributeGenerate();
        }
        return true;
    }

    public void dealDBObject(DBObject object) {
        try {
            typeWriter.write("\t\t<insert tableName=\"OBJECT_TYPES\">\n" +
                    "\t\t\t<column name=\"OBJECT_TYPE_ID\" value=\"" +
                    object.getId() + "\"/>\n" +
                    "\t\t\t<column name=\"NAME\" value=\"" +
                    object.getName() + "\"/>\n" +
                    "\t\t\t<column name=\"PARENT_ID\" value=\"" +
                    object.getParentId() + "\"/>\n" +
                    "\t\t</insert>\n");
            typeWriter.flush();
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        List<? extends Element> list = object.getAnClass().getEnclosedElements();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getAnnotation(Attribute.class) != null) {
                DBAttribute attribute = new DBAttribute();
                attribute.setId(KeyGenerator.generate());
                attribute.setName(list.get(i).getSimpleName().toString());
                Attribute attrType = list.get(i).getAnnotation(Attribute.class);
                attribute.setType(attrType.value().name());
                attributes.add(attribute);

                ObjectTypeAttribute objectAttribute = new ObjectTypeAttribute();
                objectAttribute.setAttributeId(getAttributeId(attributes, attribute));
                objectAttribute.setObjectTypeId(BigInteger.valueOf(object.getObjectType().id()));
                objectAttributes.add(objectAttribute);
            }
        }
    }

    public void attributesGenerate() {
        for (DBAttribute attribute : attributes) {
            try {
                attributeWriter.write("\t\t<insert tableName=\"ATTRIBUTES\">\n" +
                        "\t\t\t<column name=\"ATTRIBUTE_ID\" value=\"" + attribute.getId() + "\"/>\n" +
                        "\t\t\t<column name=\"NAME\" value=\"" + attribute.getName() + "\"/>\n" +
                        "\t\t\t<column name=\"TYPE_NAME\" value=\"" + attribute.getType() + "\"/>\n" +
                        "\t\t</insert>\n");
                attributeWriter.flush();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        try {
            attributeWriter.write("\t</changeSet>\n" +
                    "</databaseChangeLog>");
            attributeWriter.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void objectAttributeGenerate() {
        for (int i = 0; i < objectAttributes.size(); i++) {
            try {
                objectAttributeWriter.write("\t\t<insert tableName=\"OBJECT_TYPE_ATTRIBUTES\">\n" +
                        "\t\t\t<column name=\"OBJECT_TYPE_ID\" value=\"" +
                        objectAttributes.get(i).getObjectTypeId() + "\"/>\n" +
                        "\t\t\t<column name=\"ATTRIBUTE_ID\" value=\"" +
                        objectAttributes.get(i).getAttributeId() + "\"/>\n" +
                        "\t\t</insert>\n");
                objectAttributeWriter.flush();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        try {
            objectAttributeWriter.write("    </changeSet>\n" +
                    "</databaseChangeLog>");
            objectAttributeWriter.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public BigInteger getAttributeId(Set<DBAttribute> attributes, DBAttribute attribute) {
        for (DBAttribute attr : attributes) {
            if (attr.equals(attribute)) {
                return attr.getId();
            }
        }
        return attribute.getId();
    }

    public boolean containId(List<DBObject> list, DBObject object) {
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getId().equals(object.getParentId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
