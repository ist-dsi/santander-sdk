package org.fenixedu.santandersdk.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fenixedu.santandersdk.dto.CardPreviewBean;
import org.fenixedu.santandersdk.dto.CreateRegisterRequest;
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
    public SantanderLineGenerator(SantanderEntryValidator santanderEntryValidator) {
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

    public CardPreviewBean generateLine(CreateRegisterRequest request) throws SantanderValidationException {
        /*
         * 1. Teacher
         * 2. Researcher
         * 3. Employee
         * 4. GrantOwner
         * 5. Student
         */

        List<String> roles = request.getRoles();

        if (roles.contains("STUDENT")) {
            return createLine(request, "STUDENT");
        } else if (roles.contains("TEACHER")) {
            return createLine(request, "TEACHER");
        } else if (roles.contains("RESEARCHER")) {
            return createLine(request, "RESEARCHER");
        } else if (roles.contains("EMPLOYEE")) {
            return createLine(request, "EMPLOYEE");
        } else if (roles.contains("GRANT_OWNER")) {
            return createLine(request, "GRANT_OWNER");
        } else {
            throw new SantanderValidationException("Person has no valid role");
        }
    }

    private CardPreviewBean createLine(CreateRegisterRequest request, String role) throws SantanderValidationException {
        List<String> errors = new ArrayList<>();
        if (Strings.isNullOrEmpty(request.getUsername())) {
            errors.add("Missing username");
        }

        if (Strings.isNullOrEmpty(request.getName())) {
            errors.add("Missing name");
        }

        if (Strings.isNullOrEmpty(request.getCampus())) {
            errors.add("Missing campus");
        }

        if (role.equals("TEACHER") && Strings.isNullOrEmpty(request.getDepartmentAcronym())) {
            errors.add("Missing department acronym");
        }

        if (request.getPhoto() == null) {
            errors.add("Missing photo");
        }

        if (!errors.isEmpty()) {
            String errors_message = String.join("\n", errors);
            throw new SantanderValidationException(errors_message);
        }

        CardPreviewBean cardPreviewBean = new CardPreviewBean();

        List<String> values = new ArrayList<>();

        String recordType = "2";

        String idNumber = request.getUsername();

        String[] names = harvestNames(request.getName());
        String name = names[0];
        String surname = names[1];
        String middleNames = names[2];

        String degreeCode = "";

        CampusAddress campusAddr = campi.get(request.getCampus().toLowerCase());

        if (campusAddr == null) {
            throw new SantanderValidationException("Person has no associated campus");
        }

        String address1 = campusAddr.getAddress();
        String address2 = IST_FULL_NAME;

        String zipCode = campusAddr.getZip();
        String town = campusAddr.getTown();

        String homeCountry = "";

        String residenceCountry = request.getUsername(); // As stipulated this field will carry the istId instead.

        DateTime now = DateTime.now();

        DateTime expireDate_dateTime =
                now.plusYears(3).dayOfMonth().withMaximumValue().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);

        String expireDate = now.toString("yyyy") + "/" + expireDate_dateTime.toString("yyyy");

        String backNumber = makeZeroPaddedNumber(Integer.parseInt(request.getUsername().substring(3)), 10);

        if (backNumber == null) {
            throw new SantanderValidationException("Invalid username (username can only have up to 10 characters)");
        }

        String curricularYear = "";
        String executionYear_field = "";

        String unit = "";
        if (role.equals("TEACHER") && !Strings.isNullOrEmpty(request.getDepartmentAcronym())) {
            unit = request.getDepartmentAcronym();
        }

        String accessControl = "";

        String expireData_AAMM = expireDate_dateTime.toString("yy") + expireDate_dateTime.toString("MM");

        String templateCode = ""; //TODO

        String actionCode = request.getAction().name();

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

        cardPreviewBean.setRequestLine(santanderEntryValidator.generateLine(values));
        cardPreviewBean.setExpiryDate(expireDate_dateTime);
        cardPreviewBean.setCardName(cardName);
        cardPreviewBean.setIdentificationNumber(idNumber);
        cardPreviewBean.setPhoto(request.getPhoto());
        cardPreviewBean.setRole(roleDesc);

        return cardPreviewBean;
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
