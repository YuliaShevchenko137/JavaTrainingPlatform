package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.DateAttr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;

/**
 * Created by Клиент on 18.05.2017.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Task extends DBObject {
    @DateAttr
    Calendar endDate;
    @DateAttr
    Calendar beginDate;
}
