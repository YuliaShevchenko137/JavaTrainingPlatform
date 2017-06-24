package com.netcracker.lab3.jtp;

import org.junit.Test;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class KeyGeneratorTest{

    @Test
    public void genaratedKeyMustBeUniq(){
        Set<BigInteger> keys = new HashSet<>();
        for (int i = 0; i < 2000; i++) {
            keys.add(KeyGenerator.generate());
        }
        assertEquals("Must generate 2000 uniq keys", 2000, keys.size());
    }

}
