package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.RefObjectIdAttr;
import com.netcracker.lab3.jtp.annotations.XmlAttr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Test extends Question {
    @RefObjectIdAttr
    private TestType type;
    @XmlAttr
    private String content;
}
