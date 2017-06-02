package com.netcracker.lab3.jtp;

import java.math.BigInteger;

public class ParameterReferencesImpl implements ParameterReference{
    private BigInteger id;
    private Object value;

    @Override
    public BigInteger getId() {
        return id;
    }

    @Override
    public void setId(BigInteger id) {
        this.id = id;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }
}
