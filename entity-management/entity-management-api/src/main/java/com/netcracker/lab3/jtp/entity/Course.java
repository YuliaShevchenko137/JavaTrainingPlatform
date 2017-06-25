package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotation.Attribute;
import com.netcracker.lab3.jtp.enums.AttributeType;
import com.netcracker.lab3.jtp.annotation.DBObjectType;
import lombok.*;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@DBObjectType(id = 13)
public class Course extends DBObject {
    @Attribute(AttributeType.Date)
    private Calendar endDate;
    @Attribute(AttributeType.Date)
    private Calendar beginDate;
    @Attribute(AttributeType.List)
    private List<BigInteger> userList;
}
