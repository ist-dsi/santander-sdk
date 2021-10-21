package org.fenixedu.santandersdk.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.santandersdk.exception.SantanderValidationException;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;

@Service
public class SantanderEntryValidator {

    private static class SantanderFieldValidator {

        private final boolean required;
        private final boolean numeric;
        private final int size;
        private final String fieldName;

        public SantanderFieldValidator(final String fieldName, final boolean numeric, final int size, final boolean required) {
            this.fieldName = fieldName;
            this.numeric = numeric;
            this.size = size;
            this.required = required;
        }

        public void validate(final String s) throws SantanderValidationException {
            if (Strings.isNullOrEmpty(s)) {
                if (isRequired()) {
                    throw new SantanderValidationException("property " + fieldName + " is missing");
                } else {
                    return;
                }
            }

            if (s.length() > size) {
                final String error = String.format("property %s (%s) has to many characters (max characters: %d)", fieldName, s, size);
                throw new SantanderValidationException(error);
            }

            if (isNumeric() && !StringUtils.isNumeric(s)) {
                final String error = String.format("property %s (%s) can only contain numbers", fieldName, s);
                throw new SantanderValidationException(error);
            }
        }

        public String getFieldName() {
            return fieldName;
        }

        public boolean isRequired() {
            return required;
        }

        public boolean isNumeric() {
            return numeric;
        }

        public int getSize() {
            return size;
        }
    }

    private final List<SantanderFieldValidator> validators = new ArrayList<SantanderFieldValidator>() {
        {
            add(new SantanderFieldValidator("record_type", true, 1, true)); // 0: record type
            add(new SantanderFieldValidator("id_number", false, 10, true)); // 1: id number
            add(new SantanderFieldValidator("first_name", false, 15, true)); // 2: first name
            add(new SantanderFieldValidator("surname", false, 15, true)); // 3: surname
            add(new SantanderFieldValidator("middle_names", false, 40, false)); // 4: middle names
            add(new SantanderFieldValidator("address_1", false, 50, true)); // 5: address 1
            add(new SantanderFieldValidator("address_2", false, 50, false)); // 6: address 2
            add(new SantanderFieldValidator("zip_code", false, 8, true)); // 7: zip code
            add(new SantanderFieldValidator("town", false, 30, true)); // 8: town
            add(new SantanderFieldValidator("home_country", false, 10, false)); // 9: home country
            add(new SantanderFieldValidator("residence_country", false, 10, true)); // 10: residence country
            add(new SantanderFieldValidator("expire_date", false, 9, false)); // 11: expire date
            add(new SantanderFieldValidator("degree_code", false, 16, false)); // 12: degree code
            add(new SantanderFieldValidator("back_number", true, 10, true)); // 13: back number
            add(new SantanderFieldValidator("curricular_year", true, 2, false)); // 14: curricular year
            add(new SantanderFieldValidator("execution_year", true, 8, false)); // 15: execution year
            add(new SantanderFieldValidator("unit", false, 30, false)); // 16: unit
            add(new SantanderFieldValidator("access_control", false, 10, false)); // 17: access control
            add(new SantanderFieldValidator("expire_date", false, 4, true)); // 18: expire date
            add(new SantanderFieldValidator("template_code", false, 10, false)); // 19: template code
            add(new SantanderFieldValidator("action_code", false, 4, true)); // 20: action code
            add(new SantanderFieldValidator("role_code", true, 2, true)); // 21: role code
            add(new SantanderFieldValidator("role_desc", false, 20, false)); // 22: role desc
            add(new SantanderFieldValidator("id_document_type", true, 1, true)); // 23: id document type
            add(new SantanderFieldValidator("check_digit", true, 1, false)); // 24: check digit
            add(new SantanderFieldValidator("card_type", false, 2, true)); // 25: card type
            add(new SantanderFieldValidator("expedition_code", false, 2, true)); // 26: expedition code
            add(new SantanderFieldValidator("detour_address_1", false, 50, false)); // 27: detour address 1
            add(new SantanderFieldValidator("detour_address_2", false, 50, false)); // 28: detour address 2
            add(new SantanderFieldValidator("detour_address_3", false, 50, false)); // 29: detour address 3
            add(new SantanderFieldValidator("detour_zip_code", false, 8, false)); // 30: detour zip code
            add(new SantanderFieldValidator("detour_town", false, 30, false)); // 31: detour town
            add(new SantanderFieldValidator("additional_data", true, 1, true)); // 32: additional data
            add(new SantanderFieldValidator("card_name", false, 40, false)); // 33: card name
            add(new SantanderFieldValidator("email", false, 100, false)); // 34: email
            add(new SantanderFieldValidator("phone", false, 20, false)); // 35: phone
            add(new SantanderFieldValidator("photo_flag", true, 1, false)); // 36: photo flag
            add(new SantanderFieldValidator("photo_ref", false, 32, false)); // 37: photo ref
            add(new SantanderFieldValidator("signature_flag", true, 1, false)); // 38: signature flag
            add(new SantanderFieldValidator("signature_ref", false, 32, false)); // 39: signature ref
            add(new SantanderFieldValidator("dig_certificate_flag", true, 1, false)); // 40: dig certificate flag
            add(new SantanderFieldValidator("dig_certificate_ref", false, 32, false)); // 41: dig certificate ref
            add(new SantanderFieldValidator("filler", false, 681, false)); // 42: filler
            add(new SantanderFieldValidator("end_flag", false, 1, true)); // 43: end flag
        }
    };

    public String generateLine(final List<String> values) throws SantanderValidationException {
        final List<String> errors = new ArrayList<>();
        final StringBuilder strBuilder = new StringBuilder(1500);
        int i = 0;

        for (final String value : values) {
            try {
                strBuilder.append(makeStringBlock(value, validators.get(i)));
            } catch (final SantanderValidationException sve) {
                errors.add(sve.getMessage());
            }
            i++;
        }

        if (!errors.isEmpty()) {
            final String errorsMessage = Joiner.on("\n").join(errors);
            throw new SantanderValidationException(errorsMessage);
        }

        return strBuilder.toString();
    }

    public JsonObject getRequestAsJson(final String line) {
        final JsonObject result = new JsonObject();
        int offset = 0;
        for (final SantanderFieldValidator validator : validators) {
            result.addProperty(validator.getFieldName(), getValue(line, offset).trim());
            offset++;
        }
        return result;
    }

    private String makeStringBlock(final String value, final SantanderFieldValidator validator)
                                   throws SantanderValidationException {
        // Validate value.
        validator.validate(value);

        final int size = validator.getSize();
        final int fillerLength = size - value.length();
        final StringBuilder blockBuilder = new StringBuilder(size);

        // Append value to block string builder.
        blockBuilder.append(value);

        for (int i = 0; i < fillerLength; i++) {
            blockBuilder.append(" ");
        }

        return blockBuilder.toString();
    }

    public String getValue(final String line, final int fieldIndex) {
        int i = 0;
        int beginIndex = 0;
        for (; i < fieldIndex; i++) {
            final SantanderFieldValidator validator = validators.get(i);
            beginIndex += validator.getSize();
        }
        final int endIndex = validators.get(i).getSize() + beginIndex;
        return line.substring(beginIndex, endIndex).trim();
    }
}

