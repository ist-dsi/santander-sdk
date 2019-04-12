package org.fenixedu.santandersdk.dto;

import org.fenixedu.santandersdk.service.SantanderLineGenerator.LineBean;
import org.joda.time.DateTime;

import pt.sibscartoes.portal.wcf.tui.dto.TUIResponseData;

public class CreateRegisterResponse {

    public enum ErrorType {
        INVALID_INFORMATION, SANTANDER_COMMUNICATION, REQUEST_REFUSED, NONE;
    }

    private ErrorType errorType;
    private String responseLine;
    private String errorDescription;
    private String requestLine;

    private String cardName;
    private DateTime cardExpiryDate;    
    private byte[] photo;

    public CreateRegisterResponse(ErrorType errorType, String responseLine, String errorDescription) {
        setErrorType(errorType);
        setResponseLine(responseLine);
        setErrorDescription(errorDescription);
    }

    public CreateRegisterResponse(LineBean request, byte[] photo, ErrorType errorType, String responseLine,
            String errorDescription) {
        setErrorType(errorType);
        setResponseLine(responseLine);
        setErrorDescription(errorDescription);
        setRequest(request);
        setPhoto(photo);
    }

    public CreateRegisterResponse(LineBean request, byte[] photo, TUIResponseData response) {
        setRequest(request);
        setPhoto(photo);

        String status = response.getStatus() == null || response.getStatus().getValue() == null ? "" : response
                .getStatus().getValue().trim();

        ErrorType errorType =
                !status.isEmpty() && !status.toLowerCase().equals("error") ? ErrorType.NONE : ErrorType.REQUEST_REFUSED;

        setErrorType(errorType);

        if (response.getTuiResponseLine() != null) {
            setResponseLine(response.getTuiResponseLine().getValue());
        }

        if (errorType != ErrorType.NONE && response.getStatusDescription() != null) {
            setErrorDescription(response.getStatusDescription().getValue());
        }
    }

    public CreateRegisterResponse() {}

    public void setRequest(LineBean request) {
        setRequestLine(request.getLine());
        setCardName(request.getCardName());
        setCardExpiryDate(request.getExpiryDate());
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public String getResponseLine() {
        return responseLine;
    }

    public void setResponseLine(String responseLine) {
        this.responseLine = responseLine;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getRequestLine() {
        return requestLine;
    }

    public void setRequestLine(String requestLine) {
        this.requestLine = requestLine;
    }

    public DateTime getCardExpiryDate() {
        return cardExpiryDate;
    }

    public void setCardExpiryDate(DateTime cardExpiryDate) {
        this.cardExpiryDate = cardExpiryDate;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
