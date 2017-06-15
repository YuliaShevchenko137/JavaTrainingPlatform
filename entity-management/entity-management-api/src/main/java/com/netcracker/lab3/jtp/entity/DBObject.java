package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotation.Attribute;
import com.netcracker.lab3.jtp.enums.AttributeType;
import com.netcracker.lab3.jtp.annotation.DBObjectType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Calendar;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DBObjectType(id = 11)
public class DBObject extends EntityImpl {
    @Attribute(AttributeType.String)
    private String name;
    @Attribute(AttributeType.String)
    private String description;
    @Attribute(AttributeType.Data)
    private Calendar creationDate;
    @Attribute(AttributeType.Integer)
    private BigInteger creatorId;
    @Attribute(AttributeType.Object)
    private State state;
}
