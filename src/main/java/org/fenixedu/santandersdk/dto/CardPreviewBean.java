package org.fenixedu.santandersdk.dto;

import org.joda.time.DateTime;

public class CardPreviewBean {

    private String requestLine;
    private String identificationNumber;
    private String cardName;
    private DateTime expiryDate;
    private String role;
    private byte[] photo;

    public CardPreviewBean() {
    }

    public String getRequestLine() {
        return requestLine;
    }

    public void setRequestLine(final String line) {
        this.requestLine = line;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(final String cardName) {
        this.cardName = cardName;
    }

    public String getIdentificationNumber() {
        return this.identificationNumber;
    }

    public void setIdentificationNumber(final String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public DateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(final DateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(final byte[] photo) {
        this.photo = photo;
    }

}
