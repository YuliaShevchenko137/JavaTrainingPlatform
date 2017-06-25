package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotation.Attribute;
import com.netcracker.lab3.jtp.enums.AttributeType;
import com.netcracker.lab3.jtp.annotation.DBObjectType;
import lombok.*;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@DBObjectType(id = 12)
public class Answer extends DBObject {
    @Attribute(AttributeType.XML)
    private String correctAnswer;

    @Attribute(AttributeType.Integer)
    private BigInteger testId;

    @Attribute(AttributeType.Integer)
    private BigInteger userId;
}
