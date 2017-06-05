package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotation.Attribute;
import com.netcracker.lab3.jtp.annotation.AttributeType;
import com.netcracker.lab3.jtp.annotation.DBObjectType;
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
@DBObjectType(id = 14)
public class Evaluating extends EntityImpl {
    @Attribute(AttributeType.Integer)
    private BigInteger userId;
    @Attribute(AttributeType.Integer)
    private BigInteger taskId;
    @Attribute(AttributeType.Decimal)
    private BigDecimal point;
}
