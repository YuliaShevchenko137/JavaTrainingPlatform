package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.DataAttr;
import com.netcracker.lab3.jtp.annotations.StringAttr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Blob;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Material  extends DBObject{
    @StringAttr
    private String type;
    @DataAttr
    private Blob data;
}
