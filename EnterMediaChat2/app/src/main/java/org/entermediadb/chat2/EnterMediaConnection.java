package org.entermediadb.chat2;

import android.os.Handler;
import android.os.HandlerThread;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.Collections;

public class EnterMediaConnection
{
    public Collection getAsList(String mqlQuery)
    {
        JSONObject ret = getResponse(mqlQuery);

        if( ret instanceof Collection)
        {
            return (Collection)ret;
        }
        return Collections.EMPTY_LIST;
    }
    private static final String JSON_IDENTIFIER = "application/json";


    public JSONObject postJson(String inUrl, JSONObject inParams)
    {
        // utilize accessToken to access protected resource in DEAL
        HttpTransport httpTransport = new NetHttpTransport();
        HttpRequestFactory requestFactory = httpTransport.createRequestFactory();

        GenericUrl url = new GenericUrl(inUrl);
        String text = inParams.toJSONString();
        InputStream stream = new ByteArrayInputStream(
                text.getBytes());
        InputStreamContent content = new InputStreamContent(JSON_IDENTIFIER,
                stream);
        HttpResponse resp = null;
        String responsetxt = null;
        try {
            HttpRequest req = requestFactory.buildPostRequest(url, content);

            resp = req.execute();
            responsetxt = resp.parseAsString();
        }
        catch(Throwable ex)
        {
            throw new EMException(ex);
        }
        // log the response
        if (resp.getStatusCode() != 200) {
            throw new EMException("Error: " + resp.getStatusCode() + " " + responsetxt);
        }
        JSONParser parser = new JSONParser();

        JSONObject response = null;
        try
        {
            response =  (JSONObject) parser.parse(responsetxt);
        }
        catch(Throwable ex)
        {
            throw new EMException(ex);
        }
        if (response.get("errorcode") != null) {
            throw new EMException("Error: " + response.get("errorcode"));
        }
        return response;
    }


    public JSONObject getResponse(String inUrl)
    {
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            GenericUrl url = new GenericUrl(inUrl);
            //url.put("googletoken", "somekey");
            //url.put("key", apikey);
            //logger.debug("Querying Freebase QUERY URL: " + url.toString());
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse;
            try {
                httpResponse = request.execute();
            } catch (HttpResponseException e) {
                e.printStackTrace();
                int statusCode = e.getStatusCode();
                //logger.error("StatusCode " + statusCode);
                //logger.error("Query URL was " + url.toString());
                //logger.error("Query was " + mqlQuery);
                if (// max limit reached for a day
                        statusCode == 403) {
                    System.exit(-1);
                }
                return null;
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return null;
            }
            JSONParser parser = new JSONParser();
            JSONObject response = (JSONObject) parser.parse(httpResponse.parseAsString());
            if(response.get("errorcode") != null )
            {
               throw new EMException("Error: " + response.get("errorcode") );
            }
            return response;
        }
        catch (Throwable ex)
        {
            throw new EMException(ex);
        }
    }




    HandlerThread handlerThread = null;

    public HandlerThread getThread()
    {
        if( handlerThread == null) {
            handlerThread = new HandlerThread("HandlerThread" + hashCode() );
            handlerThread.start();
        }
        return handlerThread;
    }

    /* Handler thread */
    public void process(final UpdateActivity inHandler)
    {
        //Runnale gets the network data. Passes it to post for UI update
        Handler requestHandler = new Handler(getThread().getLooper());
        requestHandler.post(inHandler);

    }

}
