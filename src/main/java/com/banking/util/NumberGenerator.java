package com.banking.util;
import java.math.BigInteger;
import java.util.UUID;

public class NumberGenerator {
    public static String generateApprovalCode() {
        return UUID.randomUUID().toString();
    }
    public static String generateAccountNumber() {
        String generateUUIDNo = String.format("%010d",new BigInteger(UUID.randomUUID().toString().replace("-",""),16));

        return generateUUIDNo.substring(generateUUIDNo.length() - 10);
    }
}