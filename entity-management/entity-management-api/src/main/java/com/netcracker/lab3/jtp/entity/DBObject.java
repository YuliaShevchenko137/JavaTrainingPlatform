package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.DateAttr;
import com.netcracker.lab3.jtp.annotations.IntegerAttr;
import com.netcracker.lab3.jtp.annotations.RefObjectIdAttr;
import com.netcracker.lab3.jtp.annotations.StringAttr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Calendar;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DBObject implements Entity{
    @IntegerAttr
    private BigInteger id;
    @StringAttr
    private String name;
    @StringAttr
    private String description;
    @DateAttr
    private Calendar creationDate;
    @IntegerAttr
    private BigInteger creatorId;
    @RefObjectIdAttr
    private State state;
}
