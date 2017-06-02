package com.netcracker.lab3.jtp.db;

import java.math.BigInteger;
import java.util.Calendar;

public class KeyGenerator {
    public static BigInteger generate(BigInteger serverId){
        return new BigInteger(serverId.toString() + Calendar.getInstance().getTimeInMillis());
    }
}
