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

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
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

    public EntityAnnotationProcessor(){
        super();
        try {
            String objectTypesSQLPath = "database/src/main/resources/liquibase/changeLogs/objectTypes.xml";
            typeWriter = new BufferedWriter(new FileWriter(objectTypesSQLPath, false));
            typeWriter.write("<databaseChangeLog\n" +
                    "        xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog/1.8\"\n" +
                    "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "        xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog/1.8\n" +
                    "         http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-1.8.xsd\">\n" +
                    "\n" +
                    "\t<property name=\"author\" value=\"javal3\"/>" +
                    "\n" +
                    "\t<changeSet author=\"${author}\" id=\"insert into OBJECT_TYPES\">\n");
            typeWriter.flush();

            String attributesSQLPath = "database/src/main/resources/liquibase/changeLogs/attributes.xml";
            attributeWriter = new BufferedWriter(new FileWriter(attributesSQLPath, false));
            attributeWriter.write("<databaseChangeLog\n" +
                    "        xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog/1.8\"\n" +
                    "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "        xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog/1.8\n" +
                    "         http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-1.8.xsd\">\n" +
                    "\n" +
                    "\t<property name=\"author\" value=\"javal3\"/>" +
                    "\n" +
                    "\t<changeSet author=\"${author}\" id=\"insert into ATTRIBUTES\">\n");
            attributeWriter.flush();

            String objectAttributeSQLPath = "database/src/main/resources/liquibase/changeLogs/objectTypeAttributes.xml";
            objectAttributeWriter = new BufferedWriter(new FileWriter(objectAttributeSQLPath, false));
            objectAttributeWriter.write("<databaseChangeLog\n" +
                    "        xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog/1.8\"\n" +
                    "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "        xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog/1.8\n" +
                    "         http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-1.8.xsd\">\n" +
                    "\n" +
                    "\t<property name=\"author\" value=\"javal3\"/>" +
                    "\n" +
                    "\t<changeSet author=\"${author}\" id=\"insert into OBJECT_TYPE_ATTRIBUTES\">\n");
            objectAttributeWriter.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            Set<DBAttribute> attributes = new HashSet<>();
            ArrayList<ObjectTypeAttribute> objectAttributes = new ArrayList<>();
            for (TypeElement annotation : annotations) {
                Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
                if (annotation.getSimpleName().contentEquals(DBObjectType.class.getSimpleName())) {
                    for (Element element : annotatedElements) {
                        try {
                            TypeElement anClass = (TypeElement) element;
                            DBObjectType objectType = element.getAnnotation(DBObjectType.class);
                            DeclaredType declared = (DeclaredType) anClass.getSuperclass();
                            Element supertypeElement = declared.asElement();
                            DBObjectType parent = supertypeElement.getAnnotation(DBObjectType.class);
                            typeWriter.write("\t\t<insert tableName=\"OBJECT_TYPES\">\n" +
                                    "\t\t\t<column name=\"OBJECT_TYPE_ID\" value=\"" +
                                    objectType.id() + "\"/>\n" +
                                    "\t\t\t<column name=\"NAME\" value=\"" +
                                    element.getSimpleName() + "\"/>\n" +
                                    "\t\t\t<column name=\"PARENT_ID\" value=\"" +
                                    (isNull(parent) ? null : parent.id()) + "\"/>\n" +
                                    "\t\t</insert>\n");
                            typeWriter.flush();
                            List<? extends Element> list = anClass.getEnclosedElements();
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
                                    objectAttribute.setObjectTypeId(BigInteger.valueOf(objectType.id()));
                                    objectAttributes.add(objectAttribute);
                                }
                            }
                        } catch (IOException ex) {
                            log.error(ex.getMessage());
                        }
                    }
                }
            }
            if (annotations != null && annotations.isEmpty()) {
                try {
                    typeWriter.write("\t</changeSet>\n" +
                            "</databaseChangeLog>");
                    typeWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            attributesGenerate(attributes);
            objectAttributeGenerate(objectAttributes);
        return true;
    }

    public void attributesGenerate(Set<DBAttribute> attributes){
        for (DBAttribute attribute: attributes) {
            try {
                attributeWriter.write("\t\t<insert tableName=\"ATTRIBUTES\">\n" +
                        "\t\t\t<column name=\"ATTRIBUTE_ID\" value=\"" +
                                attribute.getId() +"\"/>\n" +
                        "\t\t\t<column name=\"NAME\" value=\"" +
                                attribute.getName() +"\"/>\n" +
                        "\t\t\t<column name=\"TYPE_NAME\" value=\"" +
                                attribute.getType() +"\"/>\n" +
                        "\t\t</insert>\n");
                attributeWriter.flush();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        if(attributes.isEmpty()) {
            try {
                attributeWriter.write("\t</changeSet>\n" +
                        "</databaseChangeLog>");
                attributeWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void objectAttributeGenerate(List<ObjectTypeAttribute> objectAttributes) {
        for (int i = 0; i < objectAttributes.size(); i++) {
            try {
                objectAttributeWriter.write("\t\t<insert tableName=\"OBJECT_TYPE_ATTRIBUTES\">\n" +
                        "\t\t\t<column name=\"OBJECT_TYPE_ID\" value=\"" +
                        objectAttributes.get(i).getObjectTypeId() +"\"/>\n" +
                        "\t\t\t<column name=\"ATTRIBUTE_ID\" value=\"" +
                        objectAttributes.get(i).getAttributeId() +"\"/>\n" +
                        "\t\t</insert>\n");
                objectAttributeWriter.flush();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        if(objectAttributes.isEmpty()) {
            try {
                objectAttributeWriter.write("    </changeSet>\n" +
                        "</databaseChangeLog>");
                objectAttributeWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public BigInteger getAttributeId(Set<DBAttribute> attributes, DBAttribute attribute) {
        for (DBAttribute attr : attributes) {
            if(attr.equals(attribute)) {
                return attr.getId();
            }
        }
        return attribute.getId();
    }
}
