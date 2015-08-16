package com.gpit.android.web.feeder.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class RestClient {
	private ArrayList <NameValuePair> params;
	private ArrayList <NameValuePair> headers;
	private String body = null;
	private String url;

	
	private int responseCode;
	private String message;
	
	private String response;
	//private static final String TAG = RestClient.class.getSimpleName();
	
	public String getResponse() {
		return response;
	}
	
	public String getErrorMessage() {
		return message;
	}
	
	public int getResponseCode() {
		return responseCode;
	}
	
	public RestClient(String url) {
		this.url = url;
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
		body = new String();
	}
	
	public RestClient(String url, String apiKey) {
		this(url);
		
		params.add(new BasicNameValuePair("key", apiKey));
	}
	
	public void AddParam(String name, String Value) {
		params.add(new BasicNameValuePair(name, Value));
	}
	
	public void AddHeader(String name, String Value) {
		headers.add(new BasicNameValuePair(name, Value));
	}
	
	public void AddBody(String requestBody){
		body = requestBody;
	}
	
	public void SetSessionCookie(String name, String value){
		String cookie = name + "=" + value;
		AddHeader("Cookie", cookie);
	}
	
	public void Execute(RequestMethod method) throws Exception {
		switch (method) {
			case GET:
			{
				String combinedParams = "";
				if (!params.isEmpty()) {
					combinedParams += "?";
					for(NameValuePair p : params)
					{
						String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
						if(combinedParams.length() > 1) {
							combinedParams += "&" + paramString;
						} else {
							combinedParams += paramString;
						}
					}
				}
				HttpGet request = new HttpGet(url+combinedParams);
				
				//add headers
				for (NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());
				}
				
				executeRequest(request,url);
				break;
			}
			case POST:
			{
				String combinedParams = "";
				if (!params.isEmpty()) {
					combinedParams += "?";
					for(NameValuePair p : params)
					{
						String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
						if(combinedParams.length() > 1) {
							combinedParams += "&" + paramString;
						} else {
							combinedParams += paramString;
						}
					}
				}
				
				HttpPost request = new HttpPost(url+combinedParams);
				 
                //add headers
                for(NameValuePair h : headers)
                {
                    request.addHeader(h.getName(), h.getValue());
                }
                
                if(body != null){
                	StringEntity entity = new StringEntity(body);  
                	request.setEntity(entity);
                }

                executeRequest(request, url);
                break;
			}
		}
	}
	
	private void executeRequest (HttpUriRequest request, String url)
	{
		HttpClient client = new DefaultHttpClient();
		HttpResponse httpResponse;
		
		try {
			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			message = httpResponse.getStatusLine().getReasonPhrase();
			
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				response = convertStreamToString(instream);
				instream.close();
			}
			
		} catch (ClientProtocolException e) {
			//Log.i(TAG,"client protocol exception " + e.toString());
		} catch (IOException e) {
			//Log.i(TAG,"io exception in restclient "+ e.toString());
		}
	}
	
	private static String convertStreamToString(InputStream inputStream){
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder sb = new StringBuilder();
		
		String line = null;
		try {
			while ((line = reader.readLine())!=null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			//Log.i(TAG,"couldn't read stream" +e.toString());
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				//Log.i(TAG,"error closing stream" + e.toString());
			}
		}
		return sb.toString();
	}
}