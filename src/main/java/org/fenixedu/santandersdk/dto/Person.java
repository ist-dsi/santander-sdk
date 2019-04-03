package org.fenixedu.santandersdk.dto;

import java.util.List;

public class Person {

    private String username;

    private String name;

    private List<String> roles;

    private String campus;

    private String departmentAcronym;

    private byte[] photo;

    Person() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getDepartmentAcronym() {
        return departmentAcronym;
    }

    public void setDepartmentAcronym(String departmentAcronym) {
        this.departmentAcronym = departmentAcronym;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
