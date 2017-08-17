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
  public static FileResponse getPdf(String url, String apiKey, String uuid) throws Exception {
    FileResponse resp = new FileResponse();
    
    Client client = Client.create();
    WebResource webResource =client.resource(url);
    MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
    queryParams.add("uuid", uuid);
    queryParams.add("format", "pdf");

    ClientResponse response = webResource.queryParams(queryParams)
        .header("Content-Type", "application/pdf")
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
