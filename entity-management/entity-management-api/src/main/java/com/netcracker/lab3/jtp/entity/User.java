package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.Attribute;
import com.netcracker.lab3.jtp.annotations.AttributeType;
import com.netcracker.lab3.jtp.annotations.DBObjectType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DBObjectType(id = 24)
public class User extends EntityImpl {
    @Attribute(AttributeType.String)
    private String name;
    @Attribute(AttributeType.String)
    private String password;
    @Attribute(AttributeType.Object)
    private Role role;
}
