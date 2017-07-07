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
import com.netcracker.lab3.jtp.file.*;
import java.util.List;
import java.util.Set;

import com.netcracker.lab3.jtp.annotation.DBParameterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SupportedAnnotationTypes({"com.netcracker.lab3.jtp.annotation.DBParameterType"})
@AutoService(Processor.class)
public class ParameterTypeAnnotationProcessor extends AbstractProcessor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final static String PATH = "entity-management/entity-management-impl/src/main/resources/liquibase/changeLogs/parameterTypes.xml";
    public final static String LIQUIBASE_TOP = "entity-management/entity-management-impl/src/main/resources/liquibase/changeLogParts/changeLogBegin.txt";
    public final static String LIQUIBASE_BOTTOM = "entity-management/entity-management-impl/src/main/resources/liquibase/changeLogParts/changeLogEnd.txt";
    public final static String LIQUIBASE_INSERT = "entity-management/entity-management-impl/src/main/resources/liquibase/changeLogParts/changeLogInsert.txt";
    public final static String LIQUIBASE_COLOMN = "entity-management/entity-management-impl/src/main/resources/liquibase/changeLogParts/changeLogColomn.txt";

    public void initializeWriters() {
        try {
            FileWriter.writeFile(PATH, String.format(FileReader.readFile(LIQUIBASE_TOP), "insert into ATTRIBUTE_TYPES"));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    @SuppressWarnings({"PMD.AvoidDeeplyNestedIfStmts"})
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if(!annotations.isEmpty()) {
            initializeWriters();
            for (TypeElement annotation : annotations) { // log , isnotnull
                if (annotation.getSimpleName().contentEquals(DBParameterType.class.getSimpleName())) {
                    for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                        TypeElement anClass = (TypeElement) element;
                        dealElements(anClass.getEnclosedElements());
                    }
                }
            }
            try {
                FileWriter.appendFile(PATH, FileReader.readFile(LIQUIBASE_BOTTOM));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return true;
    }

    public void dealElements(List<? extends Element> elements){
        for (Element element: elements) {
            if (element.getKind().equals(ElementKind.ENUM_CONSTANT)) {
                try {
                    String colomn = String.format(FileReader.readFile(LIQUIBASE_COLOMN),
                            "TYPE_NAME",element.getSimpleName()).replace("\n", "");
                    String insert = String.format(FileReader.readFile(LIQUIBASE_INSERT),
                            "ATTRIBUTE_TYPES", colomn);
                    FileWriter.appendFile(PATH, insert);
                } catch (IOException ex) {
                    log.error(ex.getMessage());
                }
            }
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}