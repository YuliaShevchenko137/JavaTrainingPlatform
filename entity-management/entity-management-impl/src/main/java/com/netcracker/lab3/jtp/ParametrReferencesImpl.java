package com.netcracker.lab3.jtp;

import java.math.BigInteger;

public class ParametrReferencesImpl implements ParametrReference {
    private BigInteger objectId;
    private BigInteger attributeId;
    private Object value;

    public BigInteger getObjectId() {
        return objectId;
    }

    public void setObjectId(BigInteger objectId) {
        this.objectId = objectId;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    public BigInteger getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(BigInteger attributeId) {
        this.attributeId = attributeId;
    }
}
