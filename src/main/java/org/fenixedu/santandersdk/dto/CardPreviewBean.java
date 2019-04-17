package org.fenixedu.santandersdk.dto;

import org.joda.time.DateTime;

public class CardPreviewBean {

    private String requestLine;
    private String identificationNumber;
    private String cardName;
    private DateTime expiryDate;
    private byte[] photo;

    public CardPreviewBean() {
    }

    public String getRequestLine() {
        return requestLine;
    }

    public void setLine(String line) {
        this.requestLine = line;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getIdentificationNumber() {
        return this.identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public DateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(DateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

}
