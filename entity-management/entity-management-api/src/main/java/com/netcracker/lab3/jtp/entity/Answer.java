package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotation.Attribute;
import com.netcracker.lab3.jtp.enums.AttributeType;
import com.netcracker.lab3.jtp.annotation.DBObjectType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DBObjectType(id = 12)
public class Answer extends DBObject {
    @Attribute(AttributeType.XML)
    private String correctAnswer;

    @Attribute(AttributeType.Integer)
    private BigInteger testId;

    @Attribute(AttributeType.Integer)
    private BigInteger userId;
}
