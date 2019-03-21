package org.fenixedu.bennu;

import org.fenixedu.bennu.spring.BennuSpringModule;
import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

@BennuSpringModule(basePackages = "org.fenixedu.santander-sdk", bundles = "SantanderSdkResources")
public class SantanderSdkSpringConfiguration {

    @ConfigurationManager(description = "Identification Cards Configuration")
    public interface ConfigurationProperties {

        @ConfigurationProperty(
                key = "sibs.webService.username",
                description = "UserName used to communicate with the SIBS Web Service, which returns the ID card production state.")
        String sibsWebServiceUsername();

        @ConfigurationProperty(
                key = "sibs.webService.password",
                description = "Password used to communicate with the SIBS Web Service, which returns the ID card production state.")
        String sibsWebServicePassword();

        @ConfigurationProperty(key = "app.institution.AES128.secretKey",
                description = "Secret for Institution ID card generation", defaultValue = "aa0bbfaf79654df4")
        String appInstitutionAES128SecretKey();

        @ConfigurationProperty(key = "app.institution.PIN", description = "PIN for Institution ID card generation",
                defaultValue = "0000")
        String appInstitutionPIN();

    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }
}
