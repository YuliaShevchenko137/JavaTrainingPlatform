package com.netcracker.lab3.jtp.annotation.processors;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SupportedAnnotationTypes({"com.netcracker.lab3.jtp.annotation.DBParameterType"})
@AutoService(Processor.class)
public class ParameterTypeAnnotationProcessor extends AbstractProcessor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private BufferedWriter parameterTypeWriter;

    public ParameterTypeAnnotationProcessor(){
        super();
        try {
            String parameterTypeSQLPath = "database/src/main/resources/liquibase/changeLogs/parameterTypes.xml";
            parameterTypeWriter = new BufferedWriter(new FileWriter(parameterTypeSQLPath, false));
            parameterTypeWriter.write("<databaseChangeLog\n" +
                    "        xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog/1.8\"\n" +
                    "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "        xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog/1.8\n" +
                    "         http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-1.8.xsd\">\n" +
                    "\n" +
                    "    <property name=\"author\" value=\"javal3\"/>" +
                    "\n" +
                    "    <changeSet author=\"${author}\" id=\"insert into ATTRIBUTE_TYPES\">\n");
            parameterTypeWriter.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (annotations != null) {
            for (TypeElement annotation : annotations) {
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
                                ex.printStackTrace();
                                log.error(ex.getMessage());
                            }
                        }
                    }
                }
            }
            if (annotations.isEmpty()) {
                try {
                    parameterTypeWriter.write("\t</changeSet>\n" +
                            "</databaseChangeLog>");
                    parameterTypeWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
            }
        }
        return true;
    }

}
