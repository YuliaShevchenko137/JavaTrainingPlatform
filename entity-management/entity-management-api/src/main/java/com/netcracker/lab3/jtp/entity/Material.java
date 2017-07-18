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
@DBObjectType(id = 15)
public class Material extends DBObject {
    @Attribute(AttributeType.Object)
    private Enums type;
    @Attribute(AttributeType.Data)
    private String data;
}
