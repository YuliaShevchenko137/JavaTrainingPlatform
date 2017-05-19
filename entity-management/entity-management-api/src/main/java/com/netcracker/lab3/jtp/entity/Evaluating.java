package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.DecimalAttr;
import com.netcracker.lab3.jtp.annotations.IntegerAttr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Evaluating implements Entity {
    @IntegerAttr
    private BigInteger id;
    @IntegerAttr
    private BigInteger userId;
    @IntegerAttr
    private BigInteger taskId;
    @DecimalAttr
    private double point;
}
