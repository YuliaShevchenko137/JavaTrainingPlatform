package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.Attribute;
import com.netcracker.lab3.jtp.annotations.AttributeType;
import com.netcracker.lab3.jtp.annotations.DBObjectType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DBObjectType(id = 17)
public class Question extends DBObject {
    @Attribute(AttributeType.String)
    private String questionText;
    @Attribute(AttributeType.Decimal)
    private BigDecimal point;
    @Attribute(AttributeType.Integer)
    private BigInteger order;
}
