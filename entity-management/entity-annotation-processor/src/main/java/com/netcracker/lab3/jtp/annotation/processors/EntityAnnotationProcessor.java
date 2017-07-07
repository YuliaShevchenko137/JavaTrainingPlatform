package com.netcracker.lab3.jtp.annotation.processors;

import com.google.auto.service.AutoService;
import com.netcracker.lab3.jtp.annotation.Attribute;
import com.netcracker.lab3.jtp.annotation.DBObjectType;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import com.netcracker.lab3.jtp.KeyGenerator;
import com.netcracker.lab3.jtp.file.FileReader;
import com.netcracker.lab3.jtp.file.FileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static com.netcracker.lab3.jtp.annotation.processors.ParameterTypeAnnotationProcessor.*;

@SupportedAnnotationTypes({"com.netcracker.lab3.jtp.annotation.DBObjectType"})
@AutoService(Processor.class)
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.CyclomaticComplexity"})
public class EntityAnnotationProcessor extends AbstractProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final static String OBJECT_TYPES_PATH = "entity-management/entity-management-impl/src/main/resources/liquibase/changeLogs/objectTypes.xml";
    private final static String ATTRIBUTES_PATH = "entity-management/entity-management-impl/src/main/resources/liquibase/changeLogs/attributes.xml";
    private final static String OBJECT_ATTRIBUTE_PATH = "entity-management/entity-management-impl/src/main/resources/liquibase/changeLogs/objectTypeAttributes.xml";


    private final Set<DBAttribute> attributes = new HashSet<>();
    private final List<ObjectTypeAttribute> objectAttributes = new ArrayList<>();
    private final List<DBObject> withOutParent = new ArrayList<>();
    private final List<DBObject> withParent = new ArrayList<>();

    public void initializeWriters(){
        try {
            FileWriter.writeFile(OBJECT_TYPES_PATH,
                    String.format(FileReader.readFile(LIQUIBASE_TOP), "insert into OBJECT_TYPES"));

            FileWriter.writeFile(ATTRIBUTES_PATH,
                    String.format(FileReader.readFile(LIQUIBASE_TOP), "insert into ATTRIBUTES"));

            FileWriter.writeFile(OBJECT_ATTRIBUTE_PATH,
                    String.format(FileReader.readFile(LIQUIBASE_TOP), "insert into OBJECT_TYPE_ATTRIBUTES"));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!annotations.isEmpty()) {
            initializeWriters();
            for (TypeElement annotation : annotations) {
                if (annotation.getSimpleName().contentEquals(DBObjectType.class.getSimpleName())) {
                    dealElements(roundEnv.getElementsAnnotatedWith(annotation));
                }
                try {
                    FileWriter.appendFile(OBJECT_TYPES_PATH, FileReader.readFile(LIQUIBASE_BOTTOM));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            attributesGenerate();
            objectAttributeGenerate();
        }
        return true;
    }

    public void dealElements(Set<? extends Element> elements){
        for (Element element : elements) {
            DBObject object = new DBObject(element);
            if (isNull(object.getParentId()) || containId(withParent, object)) {
                objectTypesGenerate(object);
                withParent.add(object);
                for (int i = 0; i < withOutParent.size(); i++) {
                    if (containId(withParent, withOutParent.get(i))) {
                        objectTypesGenerate(withOutParent.get(i));
                        withParent.add(withOutParent.get(i));
                        withOutParent.remove(i);
                    }
                }
            } else {
                withOutParent.add(object);
            }
        }
    }

    public void objectTypesGenerate(DBObject object) {
        try {
            String colomns = String.format(FileReader.readFile(LIQUIBASE_COLOMN),
                    "OBJECT_TYPE_ID",object.getId()) +
                    String.format(FileReader.readFile(LIQUIBASE_COLOMN),
                            "NAME",object.getName()) +
                    String.format(FileReader.readFile(LIQUIBASE_COLOMN),
                            "PARENT_ID",object.getParentId()).replace("\n", "");
            String insert = String.format(FileReader.readFile(LIQUIBASE_INSERT),
                    "OBJECT_TYPES", colomns);
            FileWriter.appendFile(OBJECT_TYPES_PATH, insert);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        objectOrder(object);
    }

    public void objectOrder(DBObject object){
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
                String colomns = String.format(FileReader.readFile(LIQUIBASE_COLOMN),
                        "ATTRIBUTE_ID",attribute.getId()) +
                        String.format(FileReader.readFile(LIQUIBASE_COLOMN),
                                "NAME",attribute.getName()) +
                        String.format(FileReader.readFile(LIQUIBASE_COLOMN),
                                "TYPE_NAME",attribute.getType()).replace("\n", "");
                String insert = String.format(FileReader.readFile(LIQUIBASE_INSERT),
                        "ATTRIBUTES", colomns);
                FileWriter.appendFile(ATTRIBUTES_PATH, insert);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        try {
            FileWriter.appendFile(ATTRIBUTES_PATH, FileReader.readFile(LIQUIBASE_BOTTOM));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void objectAttributeGenerate() {
        for (int i = 0; i < objectAttributes.size(); i++) {
            try {
                String colomns = String.format(FileReader.readFile(LIQUIBASE_COLOMN),
                        "OBJECT_TYPE_ID",objectAttributes.get(i).getObjectTypeId()) +
                        String.format(FileReader.readFile(LIQUIBASE_COLOMN),
                                "ATTRIBUTE_ID",objectAttributes.get(i).getAttributeId()).replace("\n", "");
                String insert = String.format(FileReader.readFile(LIQUIBASE_INSERT),
                        "OBJECT_TYPE_ATTRIBUTES", colomns);
                FileWriter.appendFile(OBJECT_ATTRIBUTE_PATH, insert);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        try {
            FileWriter.appendFile(OBJECT_ATTRIBUTE_PATH, FileReader.readFile(LIQUIBASE_BOTTOM));
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