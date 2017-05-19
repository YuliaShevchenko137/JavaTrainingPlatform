package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.IntegerAttr;
import com.netcracker.lab3.jtp.annotations.RefObjectIdAttr;
import com.netcracker.lab3.jtp.annotations.StringAttr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User  implements Entity{
    @IntegerAttr
    private BigInteger id;
    @StringAttr
    private String name;
    @StringAttr
    private String password;
    @RefObjectIdAttr
    private Role role;
}
