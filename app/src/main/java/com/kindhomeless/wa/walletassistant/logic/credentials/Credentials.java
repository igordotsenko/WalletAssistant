package com.kindhomeless.wa.walletassistant.logic.credentials;

import static android.text.TextUtils.isEmpty;

public class Credentials {
    private final String email;
    private final String token;

    public Credentials(String email, String token) throws EmptyCredentialsException {
        validateCredential(email, "email");
        validateCredential(token, "token");
        this.email = email;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    private void validateCredential(String credential, String credentialName)
            throws EmptyCredentialsException {

        if (isEmpty(credential)) {
            throw new EmptyCredentialsException(String.format("%s is not entered", credentialName));
        }
    }
}
