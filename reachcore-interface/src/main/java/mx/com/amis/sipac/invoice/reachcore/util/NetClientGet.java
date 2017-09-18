package mx.com.amis.sipac.invoice.reachcore.util;

import java.io.InputStream;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.IOUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import mx.com.amis.sipac.invoice.reachcore.domain.FileResponse;

public class NetClientGet {
  public static final String PDF_FORMAT = "pdf";
  public static final String XML_FORMAT = "xml";
  public static final String PDF_CONTENT_TYPE = "application/pdf";
  public static final String XML_CONTENT_TYPE = "application/xml";
  
  public static FileResponse getPdf(String url, String apiKey, String uuid) throws Exception {
    return getFile(url, apiKey, uuid, PDF_FORMAT);
  }
  
  public static FileResponse getXml(String url, String apiKey, String uuid) throws Exception {
    return getFile(url, apiKey, uuid, XML_FORMAT);
  }
  
  public static FileResponse getFile(String url, String apiKey, String uuid, String format) throws Exception {
    FileResponse resp = new FileResponse();
    
    Client client = Client.create();
    WebResource webResource =client.resource(url);
    MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
    queryParams.add("uuid", uuid);
    queryParams.add("format", format);

    ClientResponse response = webResource.queryParams(queryParams)
        .header("Content-Type", format.equals(PDF_FORMAT) ? PDF_CONTENT_TYPE : XML_CONTENT_TYPE)
        .header("RCApiKey", apiKey)
        .get(ClientResponse.class);
    
    InputStream in = response.getEntityInputStream();

    if(response.getStatus() == 200) {
      byte[] bytes = IOUtils.toByteArray(in);
      resp.setSuccess(true);
      resp.setContents(bytes);
    } else {
      String jsonStr = response.getEntity(String.class);
      resp.setSuccess(false);
      resp.setErrorMsg(jsonStr);
    }
    
    return resp;
  }
}
