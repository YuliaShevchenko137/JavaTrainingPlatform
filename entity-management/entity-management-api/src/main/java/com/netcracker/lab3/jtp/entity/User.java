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
@DBObjectType(id = 24)
public class User extends EntityImpl {
    @Attribute(AttributeType.String)
    private String name;
    @Attribute(AttributeType.String)
    private String password;
    @Attribute(AttributeType.Object)
    private Role role;
}
