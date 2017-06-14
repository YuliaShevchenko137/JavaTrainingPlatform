package com.netcracker.lab3.jtp.annotation.processors;

import java.math.BigInteger;

public class ObjectTypeAttribute {
    private BigInteger attributeId;
    private BigInteger objectTypeId;

    public BigInteger getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(BigInteger attributeId) {
        this.attributeId = attributeId;
    }

    public BigInteger getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(BigInteger objectTypeId) {
        this.objectTypeId = objectTypeId;
    }
}
