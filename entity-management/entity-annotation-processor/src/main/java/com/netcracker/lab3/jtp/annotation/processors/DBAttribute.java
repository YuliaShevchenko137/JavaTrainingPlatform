package com.netcracker.lab3.jtp.annotation.processors;

import java.math.BigInteger;

import static java.util.Objects.nonNull;

public class DBAttribute {
    private BigInteger id;
    private String name;
    private String type;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    @SuppressWarnings("PMD.ConfusingTernary")
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof DBAttribute)) return false;

        DBAttribute attribute = (DBAttribute) object;

        if (nonNull(getName()) ? !getName().equals(attribute.getName()) : nonNull(attribute.getName())) return false;
        return nonNull(getType()) ? getType().equals(attribute.getType()) : nonNull(attribute.getType());

    }

    @Override
    @SuppressWarnings("PMD.ConfusingTernary")
    public int hashCode() {
        int result = nonNull(getName()) ? getName().hashCode() : 0;
        result = 31 * result + (nonNull(getType()) ? getType().hashCode() : 0);
        return result;
    }
}
