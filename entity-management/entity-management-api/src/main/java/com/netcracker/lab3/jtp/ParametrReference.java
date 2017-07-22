package com.netcracker.lab3.jtp;

import java.math.BigInteger;

public interface ParametrReference {
    BigInteger getObjectId();

    void setObjectId(BigInteger objectId);

    Object getValue();

    void setValue(Object object);

    public BigInteger getAttributeId();

    public void setAttributeId(BigInteger attributeId);
}
