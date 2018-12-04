package com.kindhomeless.wa.walletassistant.util.transformer;

import com.kindhomeless.wa.walletassistant.logic.transformer.TextToPaymentSmsTransformer;
import com.kindhomeless.wa.walletassistant.logic.transformer.TextToPaymentSmsTransformerImpl;
import com.kindhomeless.wa.walletassistant.logic.transformer.TransformationException;
import com.kindhomeless.wa.walletassistant.model.PaymentSms;
import com.kindhomeless.wa.walletassistant.repo.storage.PaymentPlaceRepo;
import com.kindhomeless.wa.walletassistant.repo.storage.PaymentPlaceRepoMock;
import com.kindhomeless.wa.walletassistant.repo.storage.RepositoryManager;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TextToPaymentSmsTransformerImplTest {

    private final PaymentPlaceRepo paymentPlaceRepoTestRepo
            = RepositoryManager.getInstance().getRepositoryForTest(PaymentPlaceRepo.class);
    private final TextToPaymentSmsTransformer transformer =
            new TextToPaymentSmsTransformerImpl(paymentPlaceRepoTestRepo);

    @Test
    public void uahPaymentTest() throws TransformationException {
        checkUahPayment("Vasha operatsija: 03.09.2018 21:13:36 Visa Premium/1234 1.23 UAH RAIFFEISEN ONLINE UAH dostupna suma 3.21 UAH");
        checkUahPayment("Vasha operatsija: 05.09.2018 20:36:41 Mastercard Platinum/1234 1.23 UAH MAGAZYN 0964 dostupna suma 0.00 UAH");
    }

    @Test
    public void paymentPlaceTest() throws TransformationException {
        String uberPaymentSmsText = "Vasha operatsija: 05.09.2018 20:36:41 Mastercard " +
                "Platinum/1234 1.23 UAH Uber BV dostupna suma 0.00 UAH";
        checkPaymentPlacePresent(transformer.transform(uberPaymentSmsText),
                PaymentPlaceRepoMock.UBER_PAYMENT_NAME, PaymentPlaceRepoMock.UBER_CATEGORY_NAME);

        String metropolitenPaymentSmsText = "Vasha operatsija: 24.11.2018 17:51:07 Mastercard " +
                "Platinum/1234 8.00 UAH KYIVSKYI METROPOLITEN dostupna suma 3.21 UAH";
        checkPaymentPlacePresent(transformer.transform(metropolitenPaymentSmsText),
                PaymentPlaceRepoMock.METROPOLITEN_PAYMENT_NAME,
                PaymentPlaceRepoMock.PUBLIC_TRANSPORT_CATEGORY_NAME);

        String unknownPaymentSmsText = "Vasha operatsija: 24.11.2018 17:51:07 Mastercard " +
                "Platinum/1234 8.00 UAH UNKNOWN dostupna suma 3.21 UAH";
        assertFalse(transformer.transform(unknownPaymentSmsText).getPaymentPlace().isPresent());
    }

    @Test
    public void notUahPaymentTest() throws TransformationException {
        // EUR payment sms
        checkNotUahPayment("Vasha operatsija: 03.09.2018 21:13:36 Visa Premium/1234 1.23 EUR RAIFFEISEN ONLINE dostupna suma 3.21 UAH");
        // Income sms
        checkNotUahPayment("05.09.2018 na Vash rakhunok kartkovyi 123(UAH) bulo zarakhovano sumu 0.00 UAH");
        // Verification sms
        checkNotUahPayment("Vasha operatsija verifikacii uspishna: 05.09.2018 16:02:14 Visa Premium/1234: GOOGLE *SERVICES");
        // Transfer sms
        checkNotUahPayment("Bezgotivkove zarakhuvannya: 03.09.2018 21:13:36 MC Platinum Credit/1234 1.00 UAH RAIFFEISEN ONLINE UAH dostupna suma 10.00 UAH");
        // Not parsable amount
        checkNotUahPayment("Vasha operatsija: 05.09.2018 20:36:41 Mastercard Platinum/1234 xxx UAH MAGAZYN 0964 dostupna suma 0.00 UAH");
    }

    private void checkUahPayment(String smsText) throws TransformationException {
        PaymentSms paymentSms = transformer.transform(smsText);
        assertEquals(-1.23D, paymentSms.getAmount(), 0.0001);
    }

    private void checkNotUahPayment(String smsText) {
        try {
            TextToPaymentSmsTransformer transformer = new TextToPaymentSmsTransformerImpl(paymentPlaceRepoTestRepo);
            transformer.transform(smsText);
            fail("TransformationException is expected to be thrown for sms text: " + smsText);
        } catch (TransformationException e) {
        }
    }

    private void checkPaymentPlacePresent(PaymentSms paymentSms,
                                          String expectedPaymentPlaceName,
                                          String expectedPaymentPlaceCategoryName) {

        assertTrue(paymentSms.getPaymentPlace().isPresent());
        assertEquals(expectedPaymentPlaceName, paymentSms.getPaymentPlace().get().getName());
        assertEquals(
                expectedPaymentPlaceCategoryName,
                paymentSms.getPaymentPlace().get().getAssociatedCategory().getName());
    }

}