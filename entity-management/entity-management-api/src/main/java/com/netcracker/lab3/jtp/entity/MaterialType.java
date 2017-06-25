package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotation.Attribute;
import com.netcracker.lab3.jtp.annotation.DBObjectType;
import com.netcracker.lab3.jtp.enums.AttributeType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@DBObjectType(id = 25)
public class MaterialType extends EntityImpl {
    @Attribute(AttributeType.String)
    private String name;
}
