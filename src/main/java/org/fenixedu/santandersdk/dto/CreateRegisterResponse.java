package org.fenixedu.santandersdk.dto;

import pt.sibscartoes.portal.wcf.tui.dto.TUIResponseData;

public class CreateRegisterResponse {

    public CreateRegisterResponse(TUIResponseData response) {
        String status = response.getStatus() == null || response.getStatus().getValue() == null ? "" : response
                .getStatus().getValue().trim();
        boolean registerSuccessful = !status.isEmpty() && !status.toLowerCase().equals("error");

        this.registerSuccessful = registerSuccessful;

        if (response.getTuiResponseLine() != null) {
            this.responseLine = response.getTuiResponseLine().getValue();
        }

        if (!registerSuccessful) {
            if (response.getStatusDescription() != null) {
                this.errorDescription = response.getStatusDescription().getValue();
            }
        }
    }

    public CreateRegisterResponse() {}

    public CreateRegisterResponse(boolean registerSuccessful, String responseLine, String errorDescription) {
        setRegisterSuccessful(registerSuccessful);
        setResponseLine(responseLine);
        setErrorDescription(errorDescription);
    }

    private boolean registerSuccessful;
    private String responseLine;
    private String errorDescription;

    public boolean wasRegisterSuccessful() {
        return registerSuccessful;
    }

    public void setRegisterSuccessful(boolean registerSuccessful) {
        this.registerSuccessful = registerSuccessful;
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
