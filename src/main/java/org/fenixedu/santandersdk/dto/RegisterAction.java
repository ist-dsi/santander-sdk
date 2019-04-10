package org.fenixedu.santandersdk.dto;

import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;

import java.util.Locale;

public enum RegisterAction {
    NOVO,
    REMI,
    RENU,
    ATUA,
    CANC;

    public String getName() {
        return name();
    }

    public String getLocalizedName() {
        return getLocalizedNameI18N().getContent();
    }

    public String getLocalizedName(final Locale locale) {
        return getLocalizedNameI18N().getContent(locale);
    }

    public LocalizedString getLocalizedNameI18N() {
        //TODO change or remove this methods
        return BundleUtil.getLocalizedString("resources.FenixeduIstIntegrationResources", getClass().getName() + "." + name());
    }
}
