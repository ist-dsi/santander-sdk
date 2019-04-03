package org.fenixedu.santandersdk.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fenixedu.santandersdk.dto.Person;
import org.fenixedu.santandersdk.exception.SantanderValidationException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

@Service
public class SantanderLineGenerator {

    private SantanderEntryValidator santanderEntryValidator;

    @Autowired
    public SantanderLineGenerator() {
        this.santanderEntryValidator = santanderEntryValidator;
    }

    private String alamedaAddr = "Avenida Rovisco Pais, 1";
    private String alamedaZip = "1049-001";
    private String alamedaTown = "Lisboa";
    private String tagusAddr = "Av. Prof. Doutor Aníbal Cavaco Silva";
    private String tagusZip = "2744-016";
    private String tagusTown = "Porto Salvo";
    private String itnAddr = "Estrada Nacional 10 (ao Km 139,7)";
    private String itnZip = "2695-066";
    private String itnTown = "Bobadela";
    private String IST_FULL_NAME = "Instituto Superior Técnico";

    private Map<String, CampusAddress> campi = getCampi();

    public String generateLine(Person person, String action) throws SantanderValidationException {
        /*
         * 1. Teacher
         * 2. Researcher
         * 3. Employee
         * 4. GrantOwner
         * 5. Student
         */
        String line = null;

        List<String> roles = person.getRoles();

        if (roles.contains("STUDENT")) {
            line = createLine(person, "STUDENT", action);
        } else if (roles.contains("TEACHER")) {
            line = createLine(person, "TEACHER", action);
        } else if (roles.contains("RESEARCHER")) {
            line = createLine(person, "RESEARCHER", action);
        } else if (roles.contains("EMPLOYEE")) {
            line = createLine(person, "EMPLOYEE", action);
        } else if (roles.contains("GRANT_OWNER")) {
            line = createLine(person, "GRANT_OWNER", action);
        }

        if (line == null) {
            throw new SantanderValidationException("Person has no valid role");
        }

        return line;
    }

    private String createLine(Person person, String role, String action) throws SantanderValidationException {

        List<String> values = new ArrayList<>();

        String recordType = "2";

        String idNumber = person.getUsername();

        String[] names = harvestNames(person.getName());
        String name = names[0];
        String surname = names[1];
        String middleNames = names[2];

        String degreeCode = "";

        CampusAddress campusAddr = campi.get(person.getCampus());

        if (campusAddr == null) {
            throw new SantanderValidationException("Person has no associated campus");
        }

        String address1 = campusAddr.getAddress();
        String address2 = IST_FULL_NAME;

        String zipCode = campusAddr.getZip();
        String town = campusAddr.getTown();

        String homeCountry = "";

        String residenceCountry = person.getUsername(); // As stipulated this field will carry the istId instead.

        DateTime now = DateTime.now();

        String expireDate = now.toString("yyyy") + "/" + now.plusYears(3).toString("yyyy");

        String backNumber = makeZeroPaddedNumber(Integer.parseInt(person.getUsername().substring(3)), 10);

        if (backNumber == null) {
            throw new SantanderValidationException("Invalid username (username can only have up to 10 characters)");
        }

        String curricularYear = "";
        String executionYear_field = "";

        String unit = "";
        if (role.equals("TEACHER") && !Strings.isNullOrEmpty(person.getDepartmentAcronym())) {
            unit = person.getDepartmentAcronym();
        }

        String accessControl = "";

        String expireData_AAMM = now.toString("yy") + now.toString("MM");

        String templateCode = ""; //TODO

        String actionCode = action;

        String roleCode = getRoleCode(role);

        String roleDesc = getRoleDescripriton(role);

        String idDocumentType = "0"; // TODO

        String checkDigit = ""; // TODO

        String cardType = "00"; // TODO

        String expedictionCode = "00"; // TODO

        String detourAdress1 = ""; // TODO

        String detourAdress2 = ""; // TODO

        String detourAdress3 = ""; // TODO

        String detourZipCode = ""; // TODO

        String detourTown = ""; // TODO

        String aditionalData = "1"; // TODO

        String cardName = names[0].toUpperCase() + " " + names[1].toUpperCase();

        String email = ""; // TODO

        String phone = ""; // TODO

        String photoFlag = "0"; // TODO

        String photoRef = ""; // TODO

        String signatureFlag = "0"; // TODO

        String signatureRef = ""; // TODO

        String digCertificateFlag = "0"; // TODO

        String digCertificateRef = ""; // TODO

        String filler = "";

        String endFlag = "1";

        values.add(recordType); //0
        values.add(idNumber); //1
        values.add(name); //2
        values.add(surname); //3
        values.add(middleNames); //4
        values.add(address1); //5
        values.add(address2); //6
        values.add(zipCode); //7
        values.add(town); //8
        values.add(homeCountry); //9
        values.add(residenceCountry); //10
        values.add(expireDate); //11
        values.add(degreeCode); //12
        values.add(backNumber); //13
        values.add(curricularYear); //14
        values.add(executionYear_field); //15
        values.add(unit); //16
        values.add(accessControl); //17
        values.add(expireData_AAMM); //18
        values.add(templateCode); //19
        values.add(actionCode); //20
        values.add(roleCode); //21
        values.add(roleDesc); //22
        values.add(idDocumentType); //23
        values.add(checkDigit); //24
        values.add(cardType); //25
        values.add(expedictionCode); //26
        values.add(detourAdress1); //27
        values.add(detourAdress2); //28
        values.add(detourAdress3); //29
        values.add(detourZipCode); //30
        values.add(detourTown); //31
        values.add(aditionalData); //32
        values.add(cardName); //33
        values.add(email); //34
        values.add(phone); //35
        values.add(photoFlag); //36
        values.add(photoRef); //37
        values.add(signatureFlag); //38
        values.add(signatureRef); //39
        values.add(digCertificateFlag); //40
        values.add(digCertificateRef); //41
        values.add(filler); //42
        values.add(endFlag); //43

        return santanderEntryValidator.generateLine(values);
    }

    private String getRoleCode(String role) {
        switch (role) {
        case "STUDENT":
            return "01";

        case "TEACHER":
            return "02";

        case "EMPLOYEE":
            return "03";

        default:
            return "99";
        }
    }

    private String[] harvestNames(String name) {
        String[] result = new String[3];
        String purgedName = purgeString(name); //Remove special characters
        String cleanedName = Strings.nullToEmpty(purgedName).trim();
        String[] names = cleanedName.split(" ");
        result[0] = names[0].length() > 15 ? names[0].substring(0, 15) : names[0];
        result[1] = names[names.length - 1].length() > 15 ? names[names.length - 1].substring(0, 15) : names[names.length - 1];
        String midNames = names.length > 2 ? names[1] : "";
        for (int i = 2; i < (names.length - 1); i++) {
            if (midNames.length() + names[i].length() + 1 > 40) {
                break;
            }
            midNames += " ";
            midNames += names[i];
        }
        result[2] = midNames;
        return result;
    }

    private String purgeString(final String name) {
        if (!CharMatcher.javaLetter().or(CharMatcher.whitespace()).matchesAllOf(name)) {
            final char[] ca = new char[name.length()];
            int j = 0;
            for (int i = 0; i < name.length(); i++) {
                final char c = name.charAt(i);
                if (Character.isLetter(c) || c == ' ') {
                    ca[j++] = c;
                }
            }
            return new String(ca);
        }
        return name;
    }

    private String getRoleDescripriton(String role) {
        switch (role) {
        case "STUDENT":
            return "Estudante/Student";
        case "TEACHER":
            return "Docente/Faculty";
        case "EMPLOYEE":
            return "Funcionario/Staff";
        case "RESEARCHER":
            return "Invest./Researcher";
        case "GRANT_OWNER":
            return "Bolseiro/Grant Owner";
        default:
            return "00";
        }
    }

    private String makeZeroPaddedNumber(int number, int size) {
        if (String.valueOf(number).length() > size) {
            return null;
        }
        String format = "%0" + size + "d";
        return String.format(format, number);
    }

    private class CampusAddress {
        private final String address;
        private final String zip;
        private final String town;

        CampusAddress(String address, String zip, String town) {
            this.address = address;
            this.zip = zip;
            this.town = town;
        }

        public String getAddress() {
            return address;
        }

        public String getZip() {
            return zip;
        }

        public String getTown() {
            return town;
        }
    }

    private Map<String, CampusAddress> getCampi() {
        Map<String, CampusAddress> exports = new HashMap<String, CampusAddress>();
        exports.put("alameda", new CampusAddress(alamedaAddr, alamedaZip, alamedaTown));
        exports.put("tagus", new CampusAddress(tagusAddr, tagusZip, tagusTown));
        exports.put("itn", new CampusAddress(itnAddr, itnZip, itnTown));
        return exports;
    }
}
