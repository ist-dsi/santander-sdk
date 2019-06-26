package org.fenixedu.santandersdk.service;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.santandersdk.dto.CardPreviewBean;
import org.fenixedu.santandersdk.dto.CreateRegisterRequest;
import org.fenixedu.santandersdk.dto.PickupAddress;
import org.fenixedu.santandersdk.exception.SantanderValidationException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

@Service
public class SantanderLineGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SantanderLineGenerator.class);
    private final Map<String, String> charReplacementMap = new HashMap<>();
    private final CharsetEncoder latin1CharsetEncoder = Charset.forName("ISO-8859-1").newEncoder();

    private SantanderEntryValidator santanderEntryValidator;

    @Autowired
    public SantanderLineGenerator(SantanderEntryValidator santanderEntryValidator) {
        this.santanderEntryValidator = santanderEntryValidator;
        charReplacementMap.put("ł", "l");
        charReplacementMap.put("Ł", "L");
        charReplacementMap.put("Đ", "D");
        charReplacementMap.put("đ", "d");
        charReplacementMap.put("æ", "ae");
        charReplacementMap.put("ı", "i");
        charReplacementMap.put("I", "I");
    }

    private String[] normalizeCardName(final String[] names) {
        return Arrays.stream(names).map(name -> {
            if (!latin1CharsetEncoder.canEncode(name)) {
                for (final String replacementChar : charReplacementMap.keySet()) {
                    name = name.replaceAll(replacementChar, charReplacementMap.get(replacementChar));
                }
                return StringNormalizer.normalizePreservingCapitalizedLetters(name);
            }
            return name;
        }).toArray(String[]::new);
    }

    public CardPreviewBean generateLine(CreateRegisterRequest request) throws SantanderValidationException {
        List<String> errors = new ArrayList<>();
        String role = request.getRole();

        if (Strings.isNullOrEmpty(request.getUsername())) {
            errors.add("santander.sdk.error.line.generation.missing.username");
        }

        if (Strings.isNullOrEmpty(request.getName())) {
            errors.add("santander.sdk.error.line.generation.missing.name");
        }

        if (Strings.isNullOrEmpty(request.getCampus())) {
            errors.add("santander.sdk.error.line.generation.missing.campus");
        }

        if (role.equals("TEACHER") && Strings.isNullOrEmpty(request.getDepartmentAcronym())) {
            errors.add("santander.sdk.error.line.generation.missing.department.acronym");
        }

        if (request.getPhoto() == null) {
            errors.add("santander.sdk.error.line.generation.missing.photo");
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
        String[] cardNames = normalizeCardName(names);
        
        String name = names[0];
        String surname = names[1];
        String middleNames = names[2];

        String degreeCode = "";

        PickupAddress pickupAddress = request.getPickupAddress();

        if (pickupAddress == null) {
            throw new SantanderValidationException("santander.sdk.error.line.generation.user.has.no.current.campus");
        }

        String address1 = pickupAddress.getAddress1();
        String address2 = pickupAddress.getAddress2();

        String zipCode = pickupAddress.getZipCode();
        String town = pickupAddress.getZipDescriptive();

        String homeCountry = "";

        String residenceCountry = request.getUsername(); // As stipulated this field will carry the istId instead.

        DateTime now = DateTime.now();

        DateTime expireDate_dateTime =
                now.plusYears(3).dayOfMonth().withMaximumValue().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);

        String expireDate = now.toString("yyyy") + "/" + expireDate_dateTime.toString("yyyy");

        String backNumber = makeZeroPaddedNumber(Integer.parseInt(request.getUsername().substring(3)), 10);

        if (backNumber == null) {
            throw new SantanderValidationException("santander.sdk.error.line.generation.user.invalid.username.size");
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

        String cardName = cardNames[0].toUpperCase() + " " + cardNames[1].toUpperCase();

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

        try {
            cardPreviewBean.setRequestLine(santanderEntryValidator.generateLine(values));
        } catch (SantanderValidationException sve) {
            LOGGER.error(String.format("Error generation line for user %s", request.getUsername()), sve);
            throw new SantanderValidationException("santander.sdk.error.line.generation.failed");
        }
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
    
}
