package org.fenixedu.santandersdk.dto;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.google.common.base.Strings;

import pt.sibscartoes.portal.wcf.register.info.dto.RegisterData;

public class GetRegisterResponse {

    public GetRegisterResponse(RegisterData registerData) {
        String status = registerData.getStatus().getValue();

        if (status == null) {
            status = registerData.getStatusDescription().getValue();
        }

        this.status = GetRegisterStatus.fromString(status);

        DateTime expiryDate = null;
        if (registerData.getExpiryDate() != null) {
            String expiryDateString = registerData.getExpiryDate().getValue();

            if (expiryDateString != null) {
                expiryDate = DateTime.parse(expiryDateString, DateTimeFormat.forPattern("dd-MM-yyyy"));
            }
        }

        this.expiryDate = expiryDate;

        this.mifare = registerData.getMifareNumber() == null || Strings
                .isNullOrEmpty(registerData.getMifareNumber().getValue()) ? null : registerData.getMifareNumber().getValue();
    }

    public GetRegisterResponse() {}

    private GetRegisterStatus status;
    private DateTime expiryDate;
    private String mifare;

    public GetRegisterStatus getStatus() {
        return status;
    }

    public void setStatus(GetRegisterStatus status) {
        this.status = status;
    }

    public DateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(DateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getMifare() {
        return mifare;
    }

    public void setMifare(String mifare) {
        this.mifare = mifare;
    }
}
