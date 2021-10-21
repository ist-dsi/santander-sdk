package org.fenixedu.santandersdk.dto;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.google.common.base.Strings;

import pt.sibscartoes.portal.wcf.register.info.dto.RegisterData;

public class GetRegisterResponse {
    private GetRegisterStatus status;
    private DateTime expiryDate;
    private DateTime expeditionDate;
    private String mifare;
    private String serialNumber;

    public GetRegisterResponse(final RegisterData registerData) {
        String status = registerData.getStatus().getValue();

        if (status == null) {
            status = registerData.getStatusDescription().getValue();
        }

        this.status = GetRegisterStatus.fromString(status);

        DateTime expiryDate = null;

        if (registerData.getExpiryDate() != null) {
            final String expiryDateString = registerData.getExpiryDate().getValue();

            if (expiryDateString != null) {
                expiryDate = DateTime.parse(expiryDateString, DateTimeFormat.forPattern("dd-MM-yyyy"));
            }
        }

        this.expiryDate = expiryDate;

        DateTime expeditionDate = null;

        if (registerData.getExpeditionDate() != null) {
            final String expeditionDateString = registerData.getExpeditionDate().getValue();

            if (expeditionDateString != null) {
                expeditionDate = DateTime.parse(expeditionDateString, DateTimeFormat.forPattern("dd-MM-yyyy"));
            }
        }

        this.expeditionDate = expeditionDate;

        this.mifare = registerData.getMifareNumber() == null || Strings
                .isNullOrEmpty(registerData.getMifareNumber().getValue()) ? null : registerData.getMifareNumber().getValue();

        this.serialNumber = registerData.getSerialNumber() == null || Strings
                .isNullOrEmpty(registerData.getSerialNumber().getValue()) ? null : registerData.getSerialNumber().getValue();
    }

    public GetRegisterResponse() {
    }

    public GetRegisterStatus getStatus() {
        return status;
    }

    public void setStatus(final GetRegisterStatus status) {
        this.status = status;
    }

    public DateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(final DateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public DateTime getExpeditionDate() {
        return expeditionDate;
    }

    public void setExpeditionDate(final DateTime expeditionDate) {
        this.expeditionDate = expeditionDate;
    }

    public String getMifare() {
        return mifare;
    }

    public void setMifare(final String mifare) {
        this.mifare = mifare;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(final String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
