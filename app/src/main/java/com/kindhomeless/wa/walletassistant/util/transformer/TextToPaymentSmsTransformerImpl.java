package com.kindhomeless.wa.walletassistant.util.transformer;

import android.support.annotation.NonNull;

import com.kindhomeless.wa.walletassistant.model.PaymentSms;

/**
 * Transform raw payment sms text to the metadata parts
 * Example of raw sms:
 * Vasha operatsija: 03.09.2018 21:13:36 Visa Premium/5843 0.00 UAH RAIFFEISEN ONLINE UAH dostupna suma 0.00 UAH
 */
public class TextToPaymentSmsTransformerImpl implements TextToPaymentSmsTransformer {
    private static final String PARTS_SEPARATOR = "UAH";
    private static final String WORDS_SEPARATOR = " ";
    private static final String UAH_PAYMENT_PREFIX = "Vasha operatsija:";
    private static final int MIN_EXPECTED_PARTS = 2;
    private static final int PAYMENT_AMOUNT_PART_NUMBER = 0;

    @Override
    public PaymentSms transform(@NonNull String paymentSmsTest) throws TransformationException {
        if (!paymentSmsTest.startsWith(UAH_PAYMENT_PREFIX)) {
            throw new TransformationException("Is not a UAH payment");
        }
        String[] parts = getParts(paymentSmsTest);
        return new PaymentSms(getAmount(parts));
    }

    private String[] getParts(String paymentSmsTest) throws TransformationException {
        String[] parts = paymentSmsTest.split(PARTS_SEPARATOR);

        if (parts.length < MIN_EXPECTED_PARTS) {
            throw new TransformationException("Payment SMS contains less than " + MIN_EXPECTED_PARTS + " parts");
        }

        return parts;
    }

    private double getAmount(String[] parts) throws TransformationException {
        String[] words = parts[PAYMENT_AMOUNT_PART_NUMBER].split(WORDS_SEPARATOR);
        int wordsCount = words.length;
        if (wordsCount == 0) {
            throw new TransformationException("Payment Amount SMS Part is empty");
        }

        return Double.parseDouble(words[wordsCount-1]);
    }
}
