package org.fenixedu.santandersdk.dto;

import pt.sibscartoes.portal.wcf.tui.dto.TUIResponseData;

public class CreateRegisterResponse {

    public enum ErrorType {
        SANTANDER_COMMUNICATION, REQUEST_REFUSED
    }

    private ErrorType errorType;
    private String responseLine;
    private String errorDescription;

    public CreateRegisterResponse(ErrorType errorType, String responseLine, String errorDescription) {
        setErrorType(errorType);
        setResponseLine(responseLine);
        setErrorDescription(errorDescription);
    }

    public CreateRegisterResponse(TUIResponseData response) {
        String status = response.getStatus() == null || response.getStatus().getValue() == null ? "" : response
                .getStatus().getValue().trim();

        ErrorType errorType =
                !status.isEmpty() && !status.toLowerCase().equals("error") ? null : ErrorType.REQUEST_REFUSED;

        setErrorType(errorType);

        if (response.getTuiResponseLine() != null) {
            setResponseLine(response.getTuiResponseLine().getValue());
        }

        if (errorType != null && response.getStatusDescription() != null) {
            setErrorDescription(response.getStatusDescription().getValue());
        }
    }

    public CreateRegisterResponse() {}

    public boolean wasRegisterSuccessful() {
        return errorType == null;
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
}
