/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.writer;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import project.latex.balloon.TransmittedDataKeysResource;

/**
 * This is a class for use during testing, until we have our radio comms working.
 * Until that point, we'll use http comms to do end-to-end testing of the rest
 * of the system.
 * @author dgorst
 */
public class HttpDataWriter implements DataWriter {

    private final DataModelConverter converter;
    private final List<String> dataKeys;
    private final String receiverUrl;
    
    private static final Logger logger = Logger.getLogger(HttpDataWriter.class);
    
    public HttpDataWriter(TransmittedDataKeysResource transmittedDataKeysResource, DataModelConverter converter, String receiverUrl) {
        this.converter = converter;
        this.dataKeys = transmittedDataKeysResource.getTransmittedDataKeys();
        this.receiverUrl = receiverUrl;
    }
    
    String getJsonStringFromRawData(String rawString)   {
        if (rawString == null)  {
            throw new IllegalArgumentException("Cannot convert null string");
        }
        
        byte[] encodedBytes = Base64.encodeBase64(rawString.getBytes());
        String base64String = new String(encodedBytes);
        
        Map<String, Object> data = new HashMap<>();
        data.put("_raw", base64String);
        
        Map<String, Object> body = new HashMap<>();
        body.put("data", data);
        Gson gson = new Gson();
        return gson.toJson(body);
    }
    
    void sendPostRequest(String rawString) throws IOException   {
        String jsonString = getJsonStringFromRawData(rawString);
        
        CloseableHttpClient httpclient = HttpClients.createDefault();
        StringEntity entity = new StringEntity(jsonString, ContentType.create("plain/text", Consts.UTF_8));
        HttpPost httppost = new HttpPost(receiverUrl);
        httppost.addHeader("content-type", "application/json");
        httppost.setEntity(entity);
        
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

            @Override
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                StatusLine statusLine = response.getStatusLine();
                HttpEntity entity = response.getEntity();
                if (statusLine.getStatusCode() >= 300) {
                    throw new HttpResponseException(
                            statusLine.getStatusCode(),
                            statusLine.getReasonPhrase());
                }
                if (entity == null) {
                    throw new ClientProtocolException("Response contains no content");
                }
                ContentType contentType = ContentType.getOrDefault(entity);
                Charset charset = contentType.getCharset();
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), charset));
                StringBuilder stringBuilder = new StringBuilder();
                String line = reader.readLine();
                while (line != null)    {
                    stringBuilder.append(line);
                    line = reader.readLine();
                }
                return stringBuilder.toString();
            }
        };
        
        String responseString = httpclient.execute(httppost, responseHandler);
        logger.info(responseString);
    }
    
    @Override
    public void writeData(Map<String, Object> dataModel) {
        if (dataModel == null)  {
            throw new IllegalArgumentException("Cannot write null data object");
        }
        
        String csvString = this.converter.convertDataToCsvString(dataKeys, dataModel);
        
        try {
            sendPostRequest(csvString);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }
    
}
