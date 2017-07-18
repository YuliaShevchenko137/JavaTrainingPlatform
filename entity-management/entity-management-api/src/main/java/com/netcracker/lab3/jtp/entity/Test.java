package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotation.Attribute;
import com.netcracker.lab3.jtp.enums.AttributeType;
import com.netcracker.lab3.jtp.annotation.DBObjectType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@DBObjectType(id = 21)
public class Test extends Question {
    @Attribute(AttributeType.Object)
    private Enums type;
    @Attribute(AttributeType.String)
    private String content;
}
