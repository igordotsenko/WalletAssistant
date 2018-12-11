package com.kindhomeless.wa.walletassistant.logic.transformer;

import android.support.annotation.NonNull;

import com.kindhomeless.wa.walletassistant.model.PaymentPlace;
import com.kindhomeless.wa.walletassistant.model.PaymentSms;
import com.kindhomeless.wa.walletassistant.repo.storage.PaymentPlaceRepo;

/**
 * Transform raw payment sms text to the metadata parts
 * Example of raw sms:
 * Vasha operatsija: 03.09.2018 21:13:36 Visa Premium/5843 0.00 UAH RAIFFEISEN ONLINE dostupna suma 0.00 UAH
 */
public class TextToPaymentSmsTransformerImpl implements TextToPaymentSmsTransformer {
    private static final String PARTS_SEPARATOR = "UAH";
    private static final String WORDS_SEPARATOR = " ";
    private static final String ACCOUNT_SEPARATOR = "/";
    private static final String UAH_PAYMENT_PREFIX = "Vasha operatsija:";
    private static final String PAYMENT_PLACE_POSTFIX = "dostupna suma";
    private static final int MIN_EXPECTED_PARTS = 2;
    private static final int PAYMENT_AMOUNT_PART_NUMBER = 0;
    private static final int PAYMENT_PLACE_PART_NUMBER = 1;
    private static final int ACCOUNT_INFO_SUB_PART = 1;
    private static final int ACCOUNT_INFO_WORD_INDEX = 0;

    private PaymentPlaceRepo paymentPlaceRepo;

    public TextToPaymentSmsTransformerImpl(PaymentPlaceRepo paymentPlaceRepo) {
        this.paymentPlaceRepo = paymentPlaceRepo;
    }

    @Override
    public PaymentSms transform(@NonNull String paymentSmsText) throws TransformationException {
        if (!paymentSmsText.startsWith(UAH_PAYMENT_PREFIX)) {
            throw new TransformationException("Is not an UAH payment");
        }
        String[] parts = getParts(paymentSmsText);
        return new PaymentSms(
                paymentSmsText, getAmount(parts), getPaymentPlace(parts), getAccountId(parts));
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

        return getExpenseAmount(words[wordsCount-1]);
    }

    /**
     * @return expense amount. Returned value is negative as Wallet API expects negative value for
     * expense and positive for income
     */
    private double getExpenseAmount(String amount) throws TransformationException {
        try {
            return 0 - Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            throw new TransformationException(e);
        }
    }

    private PaymentPlace getPaymentPlace(String[] parts) {
        String paymentPlaceName = parts[PAYMENT_PLACE_PART_NUMBER].split(PAYMENT_PLACE_POSTFIX)[0];
        return paymentPlaceRepo.findPaymentPlaceByName(paymentPlaceName);
    }

    private String getAccountId(String[] parts) {
        String[] subParts = parts[PAYMENT_AMOUNT_PART_NUMBER].split(ACCOUNT_SEPARATOR);
        String accountInfo = subParts[ACCOUNT_INFO_SUB_PART]
                .split(WORDS_SEPARATOR)[ACCOUNT_INFO_WORD_INDEX];

        return AccountMapper.getAccountId(accountInfo);
    }
}
