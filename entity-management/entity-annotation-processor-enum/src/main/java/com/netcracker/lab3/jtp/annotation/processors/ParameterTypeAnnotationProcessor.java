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
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import com.netcracker.lab3.jtp.annotation.DBParameterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SupportedAnnotationTypes({"com.netcracker.lab3.jtp.annotation.DBParameterType","PMD.AvoidDeeplyNestedIfStmts"})
@AutoService(Processor.class)
public class ParameterTypeAnnotationProcessor extends AbstractProcessor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private BufferedWriter parameterTypeWriter;

    public void initializeWriters() {
        try {
            Path parameterTypeXMLPath = Paths.get("database/src/main/resources/liquibase/changeLogs/parameterTypes.xml");
            parameterTypeWriter = Files.newBufferedWriter(parameterTypeXMLPath, Charset.forName("UTF-8"));
            parameterTypeWriter.write("<databaseChangeLog\n" +
                    "\t\txmlns=\"http://www.liquibase.org/xml/ns/dbchangelog/1.8\"\n" +
                    "\t\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "\t\txsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog/1.8\n" +
                    "\t\thttp://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-1.8.xsd\">\n" +
                    "\n" +
                    "\t<property name=\"author\" value=\"javal3\"/>\n" +
                    "\t<changeSet author=\"${author}\" id=\"insert into ATTRIBUTE_TYPES\">\n" +
                    "\t\t<validCheckSum>ANY</validCheckSum>\n");
            parameterTypeWriter.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    @SuppressWarnings({"PMD.AvoidDeeplyNestedIfStmts"})
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if(!annotations.isEmpty()) {
            log.debug((new Exception()).getMessage());
            initializeWriters();
            for (TypeElement annotation : annotations) {
                if (annotation.getSimpleName().contentEquals(DBParameterType.class.getSimpleName())) {
                    Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
                    for (Element element : annotatedElements) {
                        TypeElement anClass = (TypeElement) element;
                        List<? extends Element> enumParameters = anClass.getEnclosedElements();
                        for (int i = 0; i < enumParameters.size(); i++) {
                            if (enumParameters.get(i).getKind().equals(ElementKind.ENUM_CONSTANT)) {
                                try {
                                    parameterTypeWriter.write("\t\t<insert tableName=\"ATTRIBUTE_TYPES\">\n" +
                                            "\t\t\t<column name=\"TYPE_NAME\" value=\"" +
                                            enumParameters.get(i).getSimpleName() + "\"/>\n" +
                                            "\t\t</insert>\n");
                                    parameterTypeWriter.flush();
                                } catch (IOException ex) {
                                    log.error(ex.getMessage());
                                }
                            }
                        }
                    }
                }
            }
            try {
                parameterTypeWriter.write("\t</changeSet>\n" +
                        "</databaseChangeLog>");
                parameterTypeWriter.flush();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
