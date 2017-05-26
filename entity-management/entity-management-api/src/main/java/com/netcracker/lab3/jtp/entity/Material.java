package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.Attribute;
import com.netcracker.lab3.jtp.annotations.AttributeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Blob;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Material extends DBObject {
    @Attribute(AttributeType.String)
    private String type;
    @Attribute(AttributeType.Data)
    private Blob data;
}
