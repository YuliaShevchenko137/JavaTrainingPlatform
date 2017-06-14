package com.netcracker.lab3.jtp;

import java.math.BigInteger;
import java.util.Calendar;

public class KeyGenerator {
    private static int sequence = 0;

    public static int sequenceGetNext(){
        if(sequence == 999) {
            sequence = 0;
        }
        return sequence++;
    }

    public static BigInteger generate(){
        return new BigInteger(getServerNumber().toString() + Calendar.getInstance().getTimeInMillis() + sequenceGetNext());
    }

    public static BigInteger getServerNumber(){
        return new BigInteger("123456");
    }
}
