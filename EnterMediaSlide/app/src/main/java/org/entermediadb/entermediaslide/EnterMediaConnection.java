package org.entermediadb.entermediaslide;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

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
    public JSONObject getResponse(String mqlQuery) {
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            JSONParser parser = new JSONParser();
            GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/mqlread");
            url.put("query", mqlQuery);
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
            JSONObject response = (JSONObject) parser.parse(httpResponse.parseAsString());
            return response;
        }
        catch (Throwable ex)
        {
            throw new EMException(ex);
        }
    }


}
