package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.Attribute;
import com.netcracker.lab3.jtp.annotations.AttributeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Evaluating extends EntityImpl {
    @Attribute(AttributeType.Integer)
    private BigInteger id;
    @Attribute(AttributeType.Integer)
    private BigInteger userId;
    @Attribute(AttributeType.Integer)
    private BigInteger taskId;
    @Attribute(AttributeType.Decimal)
    private double point;
}
