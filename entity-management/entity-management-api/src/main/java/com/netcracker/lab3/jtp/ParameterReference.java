package com.netcracker.lab3.jtp;

import java.math.BigInteger;

public interface ParameterReference {
    BigInteger getId();

    void setId(BigInteger id);

    Object getValue();

    void setValue(Object object);
}
