package com.netcracker.lab3.jtp.enums;

import com.netcracker.lab3.jtp.annotation.DBParameterType;

@DBParameterType
public enum AttributeType {
    String,
    Data,
    Date,
    Decimal,
    Integer,
    List,
    Object,
    Parameter,
    XML;
}
