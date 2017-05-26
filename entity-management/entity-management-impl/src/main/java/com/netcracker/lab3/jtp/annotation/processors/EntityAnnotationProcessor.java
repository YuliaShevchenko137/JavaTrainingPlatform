package com.netcracker.lab3.jtp.annotation.processors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import java.sql.PreparedStatement;
import java.util.Set;

@SupportedAnnotationTypes("com.netcracker.lab3.jtp.annotations.DBObjectType")
public class EntityAnnotationProcessor extends AbstractProcessor {
    PreparedStatement objectTypesStatment;
    PreparedStatement paramsStatment;
    PreparedStatement objectTypeAttributesStatment;

    public EntityAnnotationProcessor(){
        super();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }

}
