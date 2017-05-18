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
    BigInteger id;
    @StringAttr
    String name;
    @StringAttr
    String description;
    @DateAttr
    Calendar creation_date;
    @IntegerAttr
    BigInteger creator;
    @RefObjectIdAttr
    State state;
}
