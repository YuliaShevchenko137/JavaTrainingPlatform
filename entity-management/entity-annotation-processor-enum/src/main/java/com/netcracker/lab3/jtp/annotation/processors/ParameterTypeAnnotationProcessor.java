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
            String parameterTypeSQLPath = "database/src/main/resources/DBSQLFiles/parameterTypes.sql";
            parameterTypeWriter = new BufferedWriter(new FileWriter(parameterTypeSQLPath, false));
            parameterTypeWriter.write("--liquibase formatted sql\n\n" +
                    "--changeset javal3:5\n\n");
            parameterTypeWriter.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : annotatedElements) {
                TypeElement anClass = (TypeElement) element;
                List<? extends Element> enumParameters = anClass.getEnclosedElements();
                for (int i = 0; i < enumParameters.size(); i++) {
                    if (enumParameters.get(i).getKind().equals(ElementKind.ENUM_CONSTANT)) {
                        try {
                            parameterTypeWriter.write("insert into ATTRIBUTE_TYPES values('" +
                                    enumParameters.get(i).getSimpleName() + "');\n");
                            parameterTypeWriter.flush();
                        } catch (IOException ex) {
                            log.error(ex.getMessage());
                        }
                    }
                }
            }
        }
        return true;
    }
}
