package org.fenixedu.santandersdk.dto;

public class PickupAddress {
    private String address1;
    private String address2;
    private String zipCode;
    private String zipDescriptive;

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(final String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(final String address2) {
        this.address2 = address2;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(final String zipCode) {
        this.zipCode = zipCode;
    }

    public String getZipDescriptive() {
        return zipDescriptive;
    }

    public void setZipDescriptive(final String zipDescriptive) {
        this.zipDescriptive = zipDescriptive;
    }
}
