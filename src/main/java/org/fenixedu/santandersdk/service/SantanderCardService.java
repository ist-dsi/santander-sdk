package org.fenixedu.santandersdk.service;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.fenixedu.bennu.SantanderSdkSpringConfiguration;
import org.springframework.stereotype.Service;
import pt.sibscartoes.portal.wcf.register.info.IRegisterInfoService;
import pt.sibscartoes.portal.wcf.register.info.dto.RegisterData;
import pt.sibscartoes.portal.wcf.tui.ITUIDetailService;
import pt.sibscartoes.portal.wcf.tui.dto.TUIResponseData;
import pt.sibscartoes.portal.wcf.tui.dto.TuiPhotoRegisterData;
import pt.sibscartoes.portal.wcf.tui.dto.TuiSignatureRegisterData;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

@Service
public class SantanderCardService {

    public RegisterData getRegister(String userName) {
        IRegisterInfoService port = initPort(IRegisterInfoService.class, "RegisterInfoService");

        return port.getRegister(userName);
    }

    public TUIResponseData createRegister(String tuiEntry, byte[] photo) {
        TuiPhotoRegisterData photoRegisterData = createPhoto(photo);
        TuiSignatureRegisterData signature = new TuiSignatureRegisterData();

        ITUIDetailService port = initPort(ITUIDetailService.class, "TUIDetailService");

        return port.saveRegister(tuiEntry, photoRegisterData, signature);
    }

    private TuiPhotoRegisterData createPhoto(byte[] photoContents) {
        final QName FILE_NAME =
                new QName("http://schemas.datacontract.org/2004/07/SibsCards.Wcf.Services.DataContracts", "FileName");
        final QName FILE_EXTENSION =
                new QName("http://schemas.datacontract.org/2004/07/SibsCards.Wcf.Services.DataContracts", "Extension");
        final QName FILE_CONTENTS =
                new QName("http://schemas.datacontract.org/2004/07/SibsCards.Wcf.Services.DataContracts", "FileContents");
        final QName FILE_SIZE = new QName("http://schemas.datacontract.org/2004/07/SibsCards.Wcf.Services.DataContracts", "Size");

        final String EXTENSION = ".jpeg";

        TuiPhotoRegisterData photo = new TuiPhotoRegisterData();
        photo.setFileContents(new JAXBElement<>(FILE_CONTENTS, byte[].class, photoContents));
        photo.setSize(new JAXBElement<>(FILE_SIZE, String.class, Integer.toString(photoContents.length)));
        photo.setExtension(new JAXBElement<>(FILE_EXTENSION, String.class, EXTENSION));
        photo.setFileName(new JAXBElement<>(FILE_NAME, String.class, "foto")); //TODO

        return photo;
    }

    private <T> T initPort(Class<T> serviceType, String endpoint) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(serviceType);
        factory.setAddress(String.format("https://portal.sibscartoes.pt/tstwcfv2/services/%s.svc", endpoint));
        factory.setBindingId("http://schemas.xmlsoap.org/wsdl/soap12/");
        factory.getFeatures().add(new WSAddressingFeature());
        //Add loggers to request
        factory.getInInterceptors().add(new LoggingInInterceptor());
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        T port = (T) factory.create();

        /*define WSDL policy*/
        Client client = ClientProxy.getClient(port);
        HTTPConduit http = (HTTPConduit) client.getConduit();
        //Add username and password properties
        http.getAuthorization().setUserName(SantanderSdkSpringConfiguration.getConfiguration().sibsWebServiceUsername());
        http.getAuthorization().setPassword(SantanderSdkSpringConfiguration.getConfiguration().sibsWebServicePassword());

        return port;
    }
}
