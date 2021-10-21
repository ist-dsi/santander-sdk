package org.fenixedu.santandersdk.dto;

import pt.sibscartoes.portal.wcf.tui.dto.TUIResponseData;

public class CreateRegisterResponse {

    public enum ErrorType {
        SANTANDER_COMMUNICATION("santander.sdk.error.communication.with.santander"),
        REQUEST_REFUSED("santander.sdk.error.request.refused");

        public String errorMessage;

        private ErrorType(final String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }

    }

    private ErrorType errorType;
    private String responseLine;
    private String errorDescription;

    public CreateRegisterResponse(final ErrorType errorType, final String responseLine, final String errorDescription) {
        setErrorType(errorType);
        setResponseLine(responseLine);
        setErrorDescription(errorDescription);
    }

    public CreateRegisterResponse(final TUIResponseData response) {
        final String status = response.getStatus() == null || response.getStatus().getValue() == null
                        ? "" : response.getStatus().getValue().trim();
        final ErrorType errorType = !status.isEmpty() && !status.equalsIgnoreCase("error") ? null : ErrorType.REQUEST_REFUSED;

        setErrorType(errorType);

        if (response.getTuiResponseLine() != null) {
            setResponseLine(response.getTuiResponseLine().getValue());
        }

        if (errorType != null && response.getStatusDescription() != null) {
            setErrorDescription(response.getStatusDescription().getValue());
        }
    }

    public CreateRegisterResponse() {
    }

    public boolean wasRegisterSuccessful() {
        return errorType == null;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(final ErrorType errorType) {
        this.errorType = errorType;
    }

    public String getResponseLine() {
        return responseLine;
    }

    public void setResponseLine(final String responseLine) {
        this.responseLine = responseLine;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(final String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
