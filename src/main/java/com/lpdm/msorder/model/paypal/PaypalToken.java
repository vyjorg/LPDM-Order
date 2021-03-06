package com.lpdm.msorder.model.paypal;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Kybox
 * @version 1.0
 * @since 01/12/2018
 */

public class PaypalToken {

    @JsonIgnore
    private String scope;
    private String nonce;
    private String access_token;
    private String token_type;
    private String app_id;
    private long expires_in;

    public PaypalToken() {
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    @Override
    public String toString() {
        return "PaypalToken{" +
                "scope='" + scope + '\'' +
                ", nonce='" + nonce + '\'' +
                ", access_token='" + access_token + '\'' +
                ", token_type='" + token_type + '\'' +
                ", app_id='" + app_id + '\'' +
                ", expires_in=" + expires_in +
                '}';
    }
}
