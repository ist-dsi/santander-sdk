package org.fenixedu.santandersdk.service;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.fenixedu.bennu.SantanderSdkSpringConfiguration;
import org.fenixedu.santandersdk.dto.CreateRegisterResponse;
import org.fenixedu.santandersdk.dto.GetRegisterResponse;
import org.fenixedu.santandersdk.dto.Person;
import org.fenixedu.santandersdk.exception.SantanderValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.sibscartoes.portal.wcf.register.info.IRegisterInfoService;
import pt.sibscartoes.portal.wcf.register.info.dto.RegisterData;
import pt.sibscartoes.portal.wcf.tui.ITUIDetailService;
import pt.sibscartoes.portal.wcf.tui.dto.TUIResponseData;
import pt.sibscartoes.portal.wcf.tui.dto.TuiPhotoRegisterData;
import pt.sibscartoes.portal.wcf.tui.dto.TuiSignatureRegisterData;

@Service
public class SantanderCardService {

    private SantanderLineGenerator santanderLineGenerator;

    @Autowired
    public SantanderCardService(SantanderLineGenerator santanderLineGenerator) {
        this.santanderLineGenerator = santanderLineGenerator;
    }

    private final static String NAMESPACE_URI = "http://schemas.datacontract.org/2004/07/SibsCards.Wcf.Services.DataContracts";

    // TODO: This probably should be in configurations
    private final static int CONNECTION_TIMEOUT = 5000;
    private final static int RECEIVE_TIMEOUT = 10000;

    public GetRegisterResponse getRegister(String userName) {
        IRegisterInfoService port = initPort(IRegisterInfoService.class, "RegisterInfoService");

        RegisterData registerData = port.getRegister(userName);

        return new GetRegisterResponse(registerData);
    }

    public CreateRegisterResponse createRegister(String tuiEntry, byte[] photo) {
        TuiPhotoRegisterData photoRegisterData = createPhoto(photo);
        TuiSignatureRegisterData signature = new TuiSignatureRegisterData();

        ITUIDetailService port = initPort(ITUIDetailService.class, "TUIDetailService");

        TUIResponseData response = port.saveRegister(tuiEntry, photoRegisterData, signature);

        return new CreateRegisterResponse(response);
    }

    public CreateRegisterResponse createRegister(Person person, String action) {
        //TODO validate action ?
        
        String tuiEntry = null;
        try {
            tuiEntry = santanderLineGenerator.generateLine(person, action);
        } catch (SantanderValidationException sve) {
            return new CreateRegisterResponse(false, "error", sve.getMessage());
        }

        TuiPhotoRegisterData photoRegisterData = createPhoto(person.getPhoto());
        TuiSignatureRegisterData signature = new TuiSignatureRegisterData();

        ITUIDetailService port = initPort(ITUIDetailService.class, "TUIDetailService");

        TUIResponseData response = port.saveRegister(tuiEntry, photoRegisterData, signature);

        return new CreateRegisterResponse(response);
    }

    private TuiPhotoRegisterData createPhoto(byte[] photoContents) {
        final QName FILE_NAME =
                new QName(NAMESPACE_URI, "FileName");
        final QName FILE_EXTENSION =
                new QName(NAMESPACE_URI, "Extension");
        final QName FILE_CONTENTS =
                new QName(NAMESPACE_URI, "FileContents");
        final QName FILE_SIZE = new QName(NAMESPACE_URI, "Size");

        final String EXTENSION = ".jpeg";

        TuiPhotoRegisterData photo = new TuiPhotoRegisterData();
        photo.setFileContents(new JAXBElement<>(FILE_CONTENTS, byte[].class, photoContents));
        photo.setSize(new JAXBElement<>(FILE_SIZE, String.class, Integer.toString(photoContents.length)));
        photo.setExtension(new JAXBElement<>(FILE_EXTENSION, String.class, EXTENSION));
        photo.setFileName(new JAXBElement<>(FILE_NAME, String.class, "foto"));

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
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();

        httpClientPolicy.setConnectionTimeout(CONNECTION_TIMEOUT); //Time in milliseconds
        httpClientPolicy.setReceiveTimeout(RECEIVE_TIMEOUT); //Time in milliseconds
        http.setClient(httpClientPolicy);

        //Add username and password properties
        http.getAuthorization().setUserName(SantanderSdkSpringConfiguration.getConfiguration().sibsWebServiceUsername());
        http.getAuthorization().setPassword(SantanderSdkSpringConfiguration.getConfiguration().sibsWebServicePassword());

        /*((BindingProvider)port).getRequestContext().put("javax.xml.ws.client.connectionTimeout", CONNECTION_TIMEOUT);
        ((BindingProvider)port).getRequestContext().put("javax.xml.ws.client.receiveTimeout", REQUEST_TIMEOUT);*/

        return port;
    }
}
