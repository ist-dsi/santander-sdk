package org.fenixedu.santandersdk.dto;

import pt.sibscartoes.portal.wcf.tui.dto.TUIResponseData;

public class CreateRegisterResponse {
    public static CreateRegisterResponse map(TUIResponseData response) {
        CreateRegisterResponse createRegisterResponse = new CreateRegisterResponse();

        String status = response.getStatus() == null || response.getStatus().getValue() == null ? "" : response
                .getStatus().getValue().trim();
        boolean registerSuccessful = !status.isEmpty() && !status.toLowerCase().equals("error");

        createRegisterResponse.setRegisterSuccessful(registerSuccessful);

        if (response.getTuiResponseLine() != null) {
            createRegisterResponse.setResponseLine(response.getTuiResponseLine().getValue());
        }

        if (!registerSuccessful) {
            if (response.getStatusDescription() != null) {
                createRegisterResponse.setErrorDescription(response.getStatusDescription().getValue());
            }
        }

        return createRegisterResponse;
    }

    public CreateRegisterResponse() {}

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
