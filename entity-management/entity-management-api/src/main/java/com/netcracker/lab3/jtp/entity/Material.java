package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotation.Attribute;
import com.netcracker.lab3.jtp.annotation.AttributeType;
import com.netcracker.lab3.jtp.annotation.DBObjectType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DBObjectType(id = 15)
public class Material extends DBObject {
    @Attribute(AttributeType.String)
    private String type;
    @Attribute(AttributeType.Data)
    private String data;
}
