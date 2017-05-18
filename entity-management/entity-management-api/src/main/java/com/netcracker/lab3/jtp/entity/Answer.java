package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.IntegerAttr;
import com.netcracker.lab3.jtp.annotations.XmlAttr;
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
public class Answer extends DBObject{
    @XmlAttr
    String answer;

    @IntegerAttr
    BigInteger test;

    @IntegerAttr
    BigInteger user;
}
