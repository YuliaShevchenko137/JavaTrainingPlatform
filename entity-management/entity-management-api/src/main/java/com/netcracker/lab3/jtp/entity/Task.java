package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.Attribute;
import com.netcracker.lab3.jtp.annotations.AttributeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Task extends DBObject {
    @Attribute(AttributeType.Date)
    private Calendar endDate;
    @Attribute(AttributeType.Date)
    private Calendar beginDate;
}
