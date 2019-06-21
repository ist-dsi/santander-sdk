package org.fenixedu.bennu;

import org.fenixedu.bennu.spring.BennuSpringModule;
import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

@BennuSpringModule(basePackages = "org.fenixedu.santandersdk", bundles = "SantanderSdkResources")
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

        @ConfigurationProperty(
                key = "sibs.webService.address",
                defaultValue = "https://portal.sibscartoes.pt/tstwcfv2/services"
        )
        String sibsWebServiceAddress();
    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }
}
