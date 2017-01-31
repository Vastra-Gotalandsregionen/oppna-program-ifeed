
package se.vgregion.metaservice.vocabularyservice.intsvc;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "VocabularyServiceIntServiceImplService", targetNamespace = "http://intsvc.vocabularyservice.metaservice.vgregion.se/", wsdlLocation = "http://metadataservice.vgregion.se/vocabularyservice4/VocabularyService?wsdl")
public class VocabularyServiceIntServiceImplService
    extends Service
{

    private final static URL VOCABULARYSERVICEINTSERVICEIMPLSERVICE_WSDL_LOCATION;
    private final static WebServiceException VOCABULARYSERVICEINTSERVICEIMPLSERVICE_EXCEPTION;
    private final static QName VOCABULARYSERVICEINTSERVICEIMPLSERVICE_QNAME = new QName("http://intsvc.vocabularyservice.metaservice.vgregion.se/", "VocabularyServiceIntServiceImplService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://metadataservice.vgregion.se/vocabularyservice4/VocabularyService?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        VOCABULARYSERVICEINTSERVICEIMPLSERVICE_WSDL_LOCATION = url;
        VOCABULARYSERVICEINTSERVICEIMPLSERVICE_EXCEPTION = e;
    }

    public VocabularyServiceIntServiceImplService() {
        super(__getWsdlLocation(), VOCABULARYSERVICEINTSERVICEIMPLSERVICE_QNAME);
    }

    public VocabularyServiceIntServiceImplService(WebServiceFeature... features) {
        super(__getWsdlLocation(), VOCABULARYSERVICEINTSERVICEIMPLSERVICE_QNAME, features);
    }

    public VocabularyServiceIntServiceImplService(URL wsdlLocation) {
        super(wsdlLocation, VOCABULARYSERVICEINTSERVICEIMPLSERVICE_QNAME);
    }

    public VocabularyServiceIntServiceImplService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, VOCABULARYSERVICEINTSERVICEIMPLSERVICE_QNAME, features);
    }

    public VocabularyServiceIntServiceImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public VocabularyServiceIntServiceImplService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns VocabularyService
     */
    @WebEndpoint(name = "VocabularyServiceIntServiceImplPort")
    public VocabularyService getVocabularyServiceIntServiceImplPort() {
        return super.getPort(new QName("http://intsvc.vocabularyservice.metaservice.vgregion.se/", "VocabularyServiceIntServiceImplPort"), VocabularyService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns VocabularyService
     */
    @WebEndpoint(name = "VocabularyServiceIntServiceImplPort")
    public VocabularyService getVocabularyServiceIntServiceImplPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://intsvc.vocabularyservice.metaservice.vgregion.se/", "VocabularyServiceIntServiceImplPort"), VocabularyService.class, features);
    }

    private static URL __getWsdlLocation() {
        if (VOCABULARYSERVICEINTSERVICEIMPLSERVICE_EXCEPTION!= null) {
            throw VOCABULARYSERVICEINTSERVICEIMPLSERVICE_EXCEPTION;
        }
        return VOCABULARYSERVICEINTSERVICEIMPLSERVICE_WSDL_LOCATION;
    }

}
