package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.DataAttr;
import com.netcracker.lab3.jtp.annotations.DateAttr;
import com.netcracker.lab3.jtp.annotations.StringAttr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Blob;

/**
 * Created by Клиент on 18.05.2017.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Material  extends DBObject{
    @StringAttr
    String type;
    @DataAttr
    Blob data;
}
