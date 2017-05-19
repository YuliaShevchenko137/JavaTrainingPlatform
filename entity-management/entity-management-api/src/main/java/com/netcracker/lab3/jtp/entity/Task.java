package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.DateAttr;
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
    @DateAttr
    private Calendar endDate;
    @DateAttr
    private Calendar beginDate;
}
