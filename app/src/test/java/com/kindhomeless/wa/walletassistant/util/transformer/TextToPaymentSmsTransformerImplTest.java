package com.kindhomeless.wa.walletassistant.util.transformer;

import com.kindhomeless.wa.walletassistant.logic.transformer.TextToPaymentSmsTransformer;
import com.kindhomeless.wa.walletassistant.logic.transformer.TextToPaymentSmsTransformerImpl;
import com.kindhomeless.wa.walletassistant.logic.transformer.TransformationException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TextToPaymentSmsTransformerImplTest {

    @Test
    public void uahPaymentTest() throws TransformationException {
        checkUahPayment("Vasha operatsija: 03.09.2018 21:13:36 Visa Premium/5843 1.23 UAH RAIFFEISEN ONLINE UAH dostupna suma 3.21 UAH");
        checkUahPayment("Vasha operatsija: 05.09.2018 20:36:41 Mastercard Platinum/3498 1.23 UAH MAGAZYN 0964 dostupna suma 0.00 UAH");
    }

    @Test
    public void notUahPaymentTest() throws TransformationException {
        // EUR payment sms
        checkNotUahPayment("Vasha operatsija: 03.09.2018 21:13:36 Visa Premium/5843 1.23 EUR RAIFFEISEN ONLINE dostupna suma 3.21 UAH");
        // Income sms
        checkNotUahPayment("05.09.2018 na Vash rakhunok kartkovyi 1402666500(UAH) bulo zarakhovano sumu 0.00 UAH");
        // Verification sms
        checkNotUahPayment("Vasha operatsija verifikacii uspishna: 05.09.2018 16:02:14 Visa Premium/5843: GOOGLE *SERVICES");
        // Transfer sms
        checkNotUahPayment("Bezgotivkove zarakhuvannya: 03.09.2018 21:13:36 MC Platinum Credit/2427 1.00 UAH RAIFFEISEN ONLINE UAH dostupna suma 10.00 UAH");
        // Not parsable amount
        checkNotUahPayment("Vasha operatsija: 05.09.2018 20:36:41 Mastercard Platinum/3498 xxx UAH MAGAZYN 0964 dostupna suma 0.00 UAH");
    }

    private void checkUahPayment(String smsText) throws TransformationException {
        TextToPaymentSmsTransformer transformer = new TextToPaymentSmsTransformerImpl();
        assertEquals(-1.23D, transformer.transform(smsText).getAmount(), 0.0001);
    }

    private void checkNotUahPayment(String smsText) {
        try {
            TextToPaymentSmsTransformer transformer = new TextToPaymentSmsTransformerImpl();
            transformer.transform(smsText);
            fail("TransformationException is expected to be thrown for sms text: " + smsText);
        } catch (TransformationException e) {
        }
    }
}