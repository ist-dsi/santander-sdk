package org.fenixedu.santandersdk.service;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.fenixedu.bennu.SantanderSdkSpringConfiguration;
import org.fenixedu.santandersdk.dto.CardPreviewBean;
import org.fenixedu.santandersdk.dto.CreateRegisterRequest;
import org.fenixedu.santandersdk.dto.CreateRegisterResponse;
import org.fenixedu.santandersdk.dto.CreateRegisterResponse.ErrorType;
import org.fenixedu.santandersdk.dto.GetRegisterResponse;
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
public class SantanderSdkService {

    private final SantanderLineGenerator santanderLineGenerator;

    @Autowired
    public SantanderSdkService(final SantanderLineGenerator santanderLineGenerator) {
        this.santanderLineGenerator = santanderLineGenerator;
    }

    private final static String NAMESPACE_URI = "http://schemas.datacontract.org/2004/07/SibsCards.Wcf.Services.DataContracts";

    // TODO: This probably should be in configurations
    private final static int CONNECTION_TIMEOUT = 50000;
    private final static int RECEIVE_TIMEOUT = 100000;

    public GetRegisterResponse getRegister(final String userName) {
        final IRegisterInfoService port = initPort(IRegisterInfoService.class, "RegisterInfoService");
        final RegisterData registerData = port.getRegister(userName);
        return new GetRegisterResponse(registerData);
    }

    public CardPreviewBean generateCardRequest(final CreateRegisterRequest request) throws SantanderValidationException {
        return santanderLineGenerator.generateLine(request);
    }

    public CreateRegisterResponse createRegister(final CardPreviewBean cardPreviewBean) {
        final String tuiEntry = cardPreviewBean.getRequestLine();
        final TuiPhotoRegisterData photoRegisterData = createPhoto(cardPreviewBean.getPhoto());
        final TuiSignatureRegisterData signature = new TuiSignatureRegisterData();

        final ITUIDetailService port = initPort(ITUIDetailService.class, "TUIDetailService");

        try {
            final TUIResponseData responseData = port.saveRegister(tuiEntry, photoRegisterData, signature);
            return new CreateRegisterResponse(responseData);
        } catch (final WebServiceException e) {
            return new CreateRegisterResponse(ErrorType.SANTANDER_COMMUNICATION, "santander communication error",
                    e.getMessage());
        }
    }

    private TuiPhotoRegisterData createPhoto(final byte[] photoContents) {
        final QName FILE_NAME = new QName(NAMESPACE_URI, "FileName");
        final QName FILE_EXTENSION = new QName(NAMESPACE_URI, "Extension");
        final QName FILE_CONTENTS = new QName(NAMESPACE_URI, "FileContents");
        final QName FILE_SIZE = new QName(NAMESPACE_URI, "Size");

        final String EXTENSION = ".jpeg";

        final TuiPhotoRegisterData photo = new TuiPhotoRegisterData();
        photo.setFileContents(new JAXBElement<>(FILE_CONTENTS, byte[].class, photoContents));
        photo.setSize(new JAXBElement<>(FILE_SIZE, String.class, Integer.toString(photoContents.length)));
        photo.setExtension(new JAXBElement<>(FILE_EXTENSION, String.class, EXTENSION));
        photo.setFileName(new JAXBElement<>(FILE_NAME, String.class, "foto"));

        return photo;
    }

    private <T> T initPort(final Class<T> serviceType, final String endpoint) {
        final JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(serviceType);
        factory.setAddress(
                String.format(SantanderSdkSpringConfiguration.getConfiguration().sibsWebServiceAddress() + "/%s.svc", endpoint));
        factory.setBindingId("http://schemas.xmlsoap.org/wsdl/soap12/");
        factory.getFeatures().add(new WSAddressingFeature());

        // Add loggers to request
        factory.getInInterceptors().add(new LoggingInInterceptor());
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        final T port = (T) factory.create();

        // Define WSDL policy
        final Client client = ClientProxy.getClient(port);
        final HTTPConduit http = (HTTPConduit) client.getConduit();
        final HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();

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
