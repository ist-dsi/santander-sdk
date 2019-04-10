package org.fenixedu.santandersdk.dto;

import pt.sibscartoes.portal.wcf.tui.dto.TUIResponseData;

public class CreateRegisterResponse {
    private boolean registerSuccessful;
    private String responseLine;
    private String errorDescription;
    private String requestLine;

    public CreateRegisterResponse(String requestLine, TUIResponseData response) {
        setRequestLine(requestLine);

        String status = response.getStatus() == null || response.getStatus().getValue() == null ? "" : response
                .getStatus().getValue().trim();
        boolean registerSuccessful = !status.isEmpty() && !status.toLowerCase().equals("error");

        setRegisterSuccessful(registerSuccessful);

        if (response.getTuiResponseLine() != null) {
            setResponseLine(response.getTuiResponseLine().getValue());
        }

        if (!registerSuccessful) {
            if (response.getStatusDescription() != null) {
                setErrorDescription(response.getStatusDescription().getValue());
            }
        }
    }

    public CreateRegisterResponse() {}

    public CreateRegisterResponse(boolean registerSuccessful, String responseLine, String errorDescription) {
        setRegisterSuccessful(registerSuccessful);
        setResponseLine(responseLine);
        setErrorDescription(errorDescription);
    }

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

    public String getRequestLine() {
        return requestLine;
    }

    public void setRequestLine(String requestLine) {
        this.requestLine = requestLine;
    }
}
