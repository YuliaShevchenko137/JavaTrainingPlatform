package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.DateAttr;
import com.netcracker.lab3.jtp.annotations.ListAttr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Course  extends DBObject{
    @DateAttr
    private Calendar endDate;
    @DateAttr
    private Calendar beginDate;
    @ListAttr
    private List<BigInteger> userList;
}
