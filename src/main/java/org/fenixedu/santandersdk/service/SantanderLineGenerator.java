package org.fenixedu.santandersdk.service;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.santandersdk.dto.CardPreviewBean;
import org.fenixedu.santandersdk.dto.CreateRegisterRequest;
import org.fenixedu.santandersdk.dto.PickupAddress;
import org.fenixedu.santandersdk.exception.SantanderMissingInformationException;
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
    {
        charReplacementMap.put("--", " ");
        charReplacementMap.put("-", " ");
        charReplacementMap.put("'", "");
        charReplacementMap.put("ł", "l");
        charReplacementMap.put("Ł", "L");
        charReplacementMap.put("Đ", "D");
        charReplacementMap.put("đ", "d");
        charReplacementMap.put("æ", "ae");
        charReplacementMap.put("ı", "i");
        charReplacementMap.put("I", "I");
        charReplacementMap.put("Ñ", "N");
        charReplacementMap.put("ñ", "n");
        charReplacementMap.put("ï", "i");
        charReplacementMap.put("Ï", "I");
        charReplacementMap.put("ø", "o");
        charReplacementMap.put("Ø", "O");
        charReplacementMap.put("ö", "o");
        charReplacementMap.put("Ö", "O");
        charReplacementMap.put("š", "s");
        charReplacementMap.put("Š", "S");
        charReplacementMap.put("ť", "t");
        charReplacementMap.put("Ť", "T");
        charReplacementMap.put("ć", "c");
        charReplacementMap.put("Ć", "C");
        charReplacementMap.put("ő", "o");
        charReplacementMap.put("Ő", "O");
        charReplacementMap.put("č", "c");
        charReplacementMap.put("Č", "C");
        charReplacementMap.put("ń", "n");
        charReplacementMap.put("Ń", "N");
        charReplacementMap.put("ż", "z");
        charReplacementMap.put("Ż", "Z");
        charReplacementMap.put("ž", "z");
        charReplacementMap.put("Ž", "Z");
        charReplacementMap.put("ů", "u");
        charReplacementMap.put("Ů", "U");
        charReplacementMap.put("å", "a");
        charReplacementMap.put("Å", "A");
        charReplacementMap.put("ś", "s");
        charReplacementMap.put("Ś", "S");
        charReplacementMap.put("ľ", "l");
        charReplacementMap.put("Ľ", "L");
        charReplacementMap.put("ę", "e");
        charReplacementMap.put("Ę", "E");
        charReplacementMap.put("ğ", "g");
        charReplacementMap.put("Ğ", "G");
        charReplacementMap.put("ü", "u");
        charReplacementMap.put("Ü", "U");
        charReplacementMap.put("ş", "s");
        charReplacementMap.put("Ş", "S");
        charReplacementMap.put("ä", "a");
        charReplacementMap.put("Ä", "A");
        charReplacementMap.put("ë", "e");
        charReplacementMap.put("Ë", "E");
    }

    private final CharsetEncoder latin1CharsetEncoder = StandardCharsets.ISO_8859_1.newEncoder();

    private final SantanderEntryValidator santanderEntryValidator;

    @Autowired
    public SantanderLineGenerator(final SantanderEntryValidator santanderEntryValidator) {
        this.santanderEntryValidator = santanderEntryValidator;
    }

    private String normalizeCardName(String name) {
        if (!latin1CharsetEncoder.canEncode(name) || nameContainsCharReplacements(name)) {
            for (final String replacementChar : charReplacementMap.keySet()) {
                name = name.replaceAll(replacementChar, charReplacementMap.get(replacementChar));
            }
            return StringNormalizer.normalizePreservingCapitalizedLetters(name);
        }
        return name.trim();
    }

    private boolean nameContainsCharReplacements(final String name) {
        return charReplacementMap.keySet().stream().anyMatch(name::contains);
    }

    public CardPreviewBean generateLine(final CreateRegisterRequest request) throws SantanderValidationException {
        final List<String> errors = new ArrayList<>();
        final String role = request.getRole();

        if (Strings.isNullOrEmpty(request.getUsername())) {
            errors.add("santander.sdk.error.line.generation.missing.username");
        }

        if (Strings.isNullOrEmpty(request.getCardName())) {
            errors.add("santander.sdk.error.line.generation.missing.name");
        }

        if (Strings.isNullOrEmpty(request.getFullName())) {
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
            final String errorsMessage = String.join("\n", errors);
            throw new SantanderMissingInformationException(errorsMessage);
        }

        final CardPreviewBean cardPreviewBean = new CardPreviewBean();

        final List<String> values = new ArrayList<>();

        final String recordType = "2";

        final String idNumber = request.getUsername();

        final String[] names = harvestNames(request.getFullName());
        final String cardName = normalizeCardName(request.getCardName()).toUpperCase();
        final String encodedCardName = new String(cardName.getBytes(Charset.forName("Windows-1252")), Charset.forName("Windows-1252"));

        final String name = names[0];
        final String surname = names[1];
        final String middleNames = names[2];

        final String degreeCode = "";

        final PickupAddress pickupAddress = request.getPickupAddress();

        if (pickupAddress == null) {
            throw new SantanderMissingInformationException("santander.sdk.error.line.generation.user.has.no.current.campus");
        }

        final String address1 = pickupAddress.getAddress1();
        final String address2 = pickupAddress.getAddress2();

        final String zipCode = pickupAddress.getZipCode();
        final String town = pickupAddress.getZipDescriptive();

        final String homeCountry = "";

        final String residenceCountry = request.getUsername(); // As stipulated this field will carry the istId instead.

        final DateTime now = DateTime.now();

        final DateTime expireDate_dateTime =
                now.plusYears(3).dayOfMonth().withMaximumValue().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);

        final String expireDate = now.toString("yyyy") + "/" + expireDate_dateTime.toString("yyyy");

        final String backNumber = makeZeroPaddedNumber(Integer.parseInt(request.getUsername().substring(3)), 10);

        if (backNumber == null) {
            throw new SantanderValidationException("santander.sdk.error.line.generation.user.invalid.username.size");
        }

        final String curricularYear = "";
        final String executionYear_field = "";

        String unit = "";
        if (role.equals("TEACHER") && !Strings.isNullOrEmpty(request.getDepartmentAcronym())) {
            unit = request.getDepartmentAcronym();
        }

        final String accessControl = "";

        final String expireData_AAMM = expireDate_dateTime.toString("yy") + expireDate_dateTime.toString("MM");

        final String templateCode = "";

        final String actionCode = request.getAction().name();

        final String roleCode = getRoleCode(role);

        final String roleDesc = getRoleDescription(role);

        final String idDocumentType = "0";

        final String checkDigit = "";

        final String cardType = "00";

        final String expeditionCode = "00";

        final String detourAddress1 = "";

        final String detourAddress2 = "";

        final String detourAddress3 = "";

        final String detourZipCode = "";

        final String detourTown = "";

        final String additionalData = "1";

        final String email = "";

        final String phone = "";

        final String photoFlag = "1";

        final String photoRef = "";

        final String signatureFlag = "0";

        final String signatureRef = "";

        final String digCertificateFlag = "0";

        final String digCertificateRef = "";

        final String filler = "";

        final String endFlag = "1";

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
        values.add(expeditionCode); //26
        values.add(detourAddress1); //27
        values.add(detourAddress2); //28
        values.add(detourAddress3); //29
        values.add(detourZipCode); //30
        values.add(detourTown); //31
        values.add(additionalData); //32
        values.add(encodedCardName); //33
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
        } catch (final SantanderValidationException sve) {
            LOGGER.error(String.format("Error generation line for user %s", request.getUsername()), sve);
            throw new SantanderValidationException("santander.sdk.error.line.generation.failed");
        }
        cardPreviewBean.setExpiryDate(expireDate_dateTime);
        cardPreviewBean.setCardName(encodedCardName);
        cardPreviewBean.setIdentificationNumber(idNumber);
        cardPreviewBean.setPhoto(request.getPhoto());
        cardPreviewBean.setRole(roleDesc);

        return cardPreviewBean;
    }

    private String getRoleCode(final String role) {
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

    private String[] harvestNames(final String name) {
        final String[] result = new String[3];
        final String purgedName = cleanNames(name);
        final String[] names = purgedName.split(" ");
        result[0] = names[0].length() > 15 ? names[0].substring(0, 15) : names[0];
        result[1] = names[names.length - 1].length() > 15 ? names[names.length - 1].substring(0, 15) : names[names.length - 1];
        final StringBuilder midNames = new StringBuilder(names.length > 2 ? names[1] : "");
        for (int i = 2; i < (names.length - 1); i++) {
            if (midNames.length() + names[i].length() + 1 > 40) {
                break;
            }
            midNames.append(" ");
            midNames.append(names[i]);
        }
        result[2] = midNames.toString();
        return result;
    }

    private String cleanNames(final String name) {
        return purgeString(name).trim();
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

    private String getRoleDescription(final String role) {
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

    private String makeZeroPaddedNumber(final int number, final int size) {
        if (String.valueOf(number).length() > size) {
            return null;
        }
        final String format = "%0" + size + "d";
        return String.format(format, number);
    }

}
