package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.IntegerAttr;
import com.netcracker.lab3.jtp.annotations.RefObjectIdAttr;
import com.netcracker.lab3.jtp.annotations.StringAttr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

/**
 * Created by Клиент on 18.05.2017.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User  implements Entity{
    @IntegerAttr
    BigInteger id;
    @StringAttr
    String name;
    @StringAttr
    String password;
    @RefObjectIdAttr
    Role role;
}
