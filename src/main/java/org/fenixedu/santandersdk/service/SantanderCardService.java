package org.fenixedu.santandersdk.service;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import com.google.common.io.BaseEncoding;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.fenixedu.bennu.SantanderSdkSpringConfiguration;
import org.fenixedu.santandersdk.dto.CreateRegisterRequest;
import org.fenixedu.santandersdk.dto.CreateRegisterResponse;
import org.fenixedu.santandersdk.dto.GetRegisterResponse;
import org.fenixedu.santandersdk.exception.SantanderValidationException;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.sibscartoes.portal.wcf.register.info.IRegisterInfoService;
import pt.sibscartoes.portal.wcf.register.info.dto.RegisterData;
import pt.sibscartoes.portal.wcf.tui.ITUIDetailService;
import pt.sibscartoes.portal.wcf.tui.dto.TUIResponseData;
import pt.sibscartoes.portal.wcf.tui.dto.TuiPhotoRegisterData;
import pt.sibscartoes.portal.wcf.tui.dto.TuiSignatureRegisterData;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;


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

    public CreateRegisterResponse createRegister(CreateRegisterRequest request) {
        //TODO validate action ?
        
        String tuiEntry;
        TuiPhotoRegisterData photoRegisterData;
        try {
            tuiEntry = santanderLineGenerator.generateLine(request);
            photoRegisterData = createPhoto(request.getPhoto());
        } catch (SantanderValidationException sve) {
            return new CreateRegisterResponse(false, "error", sve.getMessage());
        }

        TuiSignatureRegisterData signature = new TuiSignatureRegisterData();

        ITUIDetailService port = initPort(ITUIDetailService.class, "TUIDetailService");

        TUIResponseData responseData;
        try {
            responseData = port.saveRegister(tuiEntry, photoRegisterData, signature);
        } catch (WebServiceException e) {
            CreateRegisterResponse response = new CreateRegisterResponse(false, "communication error", e.getMessage());
            response.setRequestLine(tuiEntry);

            return response;
        }


        return new CreateRegisterResponse(tuiEntry, responseData);
    }

    public BufferedImage readImage(byte[] imageData) throws SantanderValidationException {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);

        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new SantanderValidationException("Could not read image");
        }
    }

    public byte[] writeImageAsBytes(BufferedImage image) throws SantanderValidationException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, "image/jpeg", out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new SantanderValidationException("Could not write image");
        }
    }

    private BufferedImage read(String base64Photo) throws SantanderValidationException {
        byte[] imageBinary = Base64.getDecoder().decode(base64Photo);
        BufferedImage image = readImage(imageBinary);
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        result.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
        return result;
    }

    private byte[] transform(String base64Photo) throws SantanderValidationException {
        BufferedImage image = read(base64Photo);
        final BufferedImage adjustedImage = transformZoom(image, 9, 10);
        final BufferedImage avatar = Scalr.resize(adjustedImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_EXACT, 180, 200);
        return writeImageAsBytes(avatar);
    }

    private BufferedImage transformZoom(final BufferedImage source, int xRatio, int yRatio) {
        int destW, destH;
        BufferedImage finale;
        if ((1.0 * source.getWidth() / source.getHeight()) > (1.0 * xRatio / yRatio)) {
            destH = source.getHeight();
            destW = (int) Math.round((destH * xRatio * 1.0) / (yRatio * 1.0));

            int padding = (int) Math.round((source.getWidth() - destW) / 2.0);
            finale = Scalr.crop(source, padding, 0, destW, destH);
        } else {
            destW = source.getWidth();
            destH = (int) Math.round((destW * yRatio * 1.0) / (xRatio * 1.0));

            int padding = (int) Math.round((source.getHeight() - destH) / 2.0);
            finale = Scalr.crop(source, 0, padding, destW, destH);
        }
        return finale;
    }

    private TuiPhotoRegisterData createPhoto(String base64Photo) throws SantanderValidationException {
        final QName FILE_NAME =
                new QName(NAMESPACE_URI, "FileName");
        final QName FILE_EXTENSION =
                new QName(NAMESPACE_URI, "Extension");
        final QName FILE_CONTENTS =
                new QName(NAMESPACE_URI, "FileContents");
        final QName FILE_SIZE = new QName(NAMESPACE_URI, "Size");

        final String EXTENSION = ".jpeg";

        byte[] photoContents = transform(base64Photo);

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
