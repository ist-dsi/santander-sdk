package org.fenixedu.santandersdk.dto;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import pt.sibscartoes.portal.wcf.register.info.dto.RegisterData;

public class GetRegisterResponse {
    public static GetRegisterResponse map(RegisterData registerData) {
        GetRegisterResponse getRegisterResponse = new GetRegisterResponse();

        String status = registerData.getStatus().getValue();

        if (status == null) {
            status = registerData.getStatusDescription().getValue();
        }

        getRegisterResponse.setStatus(GetRegisterStatus.fromString(status));

        DateTime expiryDate = null;
        if (registerData.getExpiryDate() != null) {
            String expiryDateString = registerData.getExpiryDate().getValue();

            if (expiryDateString != null) {
                expiryDate = DateTime.parse(expiryDateString, DateTimeFormat.forPattern("dd-MM-yyyy"));
            }
        }

        getRegisterResponse.setExpiryDate(expiryDate);

        return getRegisterResponse;
    }

    private GetRegisterResponse() {}

    private GetRegisterStatus getRegisterStatus;
    private DateTime expiryDate;

    public GetRegisterStatus getStatus() {
        return getRegisterStatus;
    }

    public void setStatus(GetRegisterStatus getRegisterStatus) {
        this.getRegisterStatus = getRegisterStatus;
    }

    public DateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(DateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
