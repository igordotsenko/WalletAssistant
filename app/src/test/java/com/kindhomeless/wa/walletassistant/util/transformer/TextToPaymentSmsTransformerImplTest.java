package com.kindhomeless.wa.walletassistant.util.transformer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TextToPaymentSmsTransformerImplTest {

    @Test
    public void uahPaymentTest() throws TransformationException {
        checkUahPayment("Vasha operatsija: 03.09.2018 21:13:36 Visa Premium/5843 1.23 UAH RAIFFEISEN ONLINE UAH dostupna suma 3.21 UAH");
        checkUahPayment("Vasha operatsija: 05.09.2018 20:36:41 Mastercard Platinum/3498 1.23 UAH MAGAZYN 0964 dostupna suma 14113.86 UAH");
    }

    @Test
    public void notUahPaymentTest() throws TransformationException {
        checkNotUahPayment("Vasha operatsija: 03.09.2018 21:13:36 Visa Premium/5843 1.23 EUR RAIFFEISEN ONLINE dostupna suma 3.21 UAH");
        checkNotUahPayment("05.09.2018 na Vash rakhunok kartkovyi 1402666500(UAH) bulo zarakhovano sumu 50.00 UAH");
        checkNotUahPayment("Vasha operatsija verifikacii uspishna: 05.09.2018 16:02:14 Visa Premium/5843: GOOGLE *SERVICES");
        checkNotUahPayment("Bezgotivkove zarakhuvannya: 03.09.2018 21:13:36 MC Platinum Credit/2427 1.00 UAH RAIFFEISEN ONLINE UAH dostupna suma 10.00 UAH");
    }

    private void checkUahPayment(String smsText) throws TransformationException {
        TextToPaymentSmsTransformer transformer = new TextToPaymentSmsTransformerImpl();
        assertEquals(1.23D, transformer.transform(smsText).getAmount(), 0.0001);
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