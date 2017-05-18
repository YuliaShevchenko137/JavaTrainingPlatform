package com.netcracker.lab3.jtp.entity;

import com.netcracker.lab3.jtp.annotations.DecimalAttr;
import com.netcracker.lab3.jtp.annotations.IntegerAttr;
import com.netcracker.lab3.jtp.annotations.StringAttr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Клиент on 18.05.2017.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Question  extends DBObject {
    @StringAttr
    String question;
    @DecimalAttr
    double point;
    @IntegerAttr
    int order;
}
