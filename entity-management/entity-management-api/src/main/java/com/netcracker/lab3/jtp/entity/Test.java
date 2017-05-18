package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.RefObjectIdAttr;
import com.netcracker.lab3.jtp.annotations.XmlAttr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Клиент on 18.05.2017.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Test extends Question {
    @RefObjectIdAttr
    TestType type;
    @XmlAttr
    String content;
}
