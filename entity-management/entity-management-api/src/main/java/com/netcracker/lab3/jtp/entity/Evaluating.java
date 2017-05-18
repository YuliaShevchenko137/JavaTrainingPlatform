package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.DecimalAttr;
import com.netcracker.lab3.jtp.annotations.IntegerAttr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

/**
 * Created by Клиент on 18.05.2017.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Evaluating implements Entity {
    @IntegerAttr
    BigInteger id;
    @IntegerAttr
    BigInteger userId;
    @IntegerAttr
    BigInteger taskId;
    @DecimalAttr
    double point;
}
