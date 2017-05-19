package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.IntegerAttr;
import com.netcracker.lab3.jtp.annotations.XmlAttr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Answer extends DBObject {
    @XmlAttr
    private String correctAnswer;

    @IntegerAttr
    private BigInteger testId;

    @IntegerAttr
    private BigInteger userId;
}
