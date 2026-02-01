package com.org.payment.util;

import com.org.payment.model.dto.request.PaymentRequest;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class RequestHashUtil {

    public static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : encoded) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Unable to hash request", e);
        }
    }

    public static String canonicalize(PaymentRequest req) {
        return req.getUserId() + "|" +
                req.getReceiverContactNumber() + "|" +
                req.getAmount() + "|" +
                req.getCurrency();
    }
}
