package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.DecimalAttr;
import com.netcracker.lab3.jtp.annotations.IntegerAttr;
import com.netcracker.lab3.jtp.annotations.StringAttr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Question  extends DBObject {
    @StringAttr
    private String questionText;
    @DecimalAttr
    private double point;
    @IntegerAttr
    private BigInteger order;
}
