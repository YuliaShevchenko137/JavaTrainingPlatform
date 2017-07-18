package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotation.Attribute;
import com.netcracker.lab3.jtp.annotation.DBObjectType;
import com.netcracker.lab3.jtp.annotation.EnumClass;
import com.netcracker.lab3.jtp.annotation.FieldName;
import com.netcracker.lab3.jtp.enums.AttributeType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@DBObjectType(id = 22)
@EnumClass
public class Enums {
    @Attribute(AttributeType.String)
    @FieldName(name = "name")//you can chose field name. but do not chose annotation name value
    private String name;
    @Attribute(AttributeType.String)
    @FieldName(name = "enumName")
    private String enumName;
}
