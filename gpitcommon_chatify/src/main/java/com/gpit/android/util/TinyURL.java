package com.gpit.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;

public class TinyURL {
	public interface OnCompletedListener {
		public void onSuccess(String url);

		public void onFail(Exception e, String msg);
	}

	private static TinyURL tinyURL = null;

	public static TinyURL getInstance(Context context) {
		if (tinyURL == null)
			tinyURL = new TinyURL(context);

		return tinyURL;
	}

	private Context mContext;
	private OnCompletedListener mCompletedListener;

	private TinyURL(Context context) {
		mContext = context;
	}

	public void getTinyURL(String url, OnCompletedListener listener) {
		mCompletedListener = listener;
		Thread thread = new Thread(new ExtendedRunnable(url) {
			@Override
			public void run() {
				String url = (String)item;
				if (!Utils.checkUrlFormat(url)) {
					if (mCompletedListener != null)
						mCompletedListener.onFail(new MalformedURLException(), null);
		            return;
		        }
		        try {
		            HttpClient client = new DefaultHttpClient();
		            String urlTemplate = "http://tinyurl.com/api-create.php?url=%s";
		            String uri = String.format(urlTemplate, URLEncoder.encode(url));
		            HttpGet request = new HttpGet(uri);
		            HttpResponse response = client.execute(request);
		            HttpEntity entity = response.getEntity();
		            InputStream in = entity.getContent();
		            try {
		                StatusLine statusLine = response.getStatusLine();
		                int statusCode = statusLine.getStatusCode();
		                if (statusCode == HttpStatus.SC_OK) {
		                    // TODO: Support other encodings
		                    String enc = "utf-8";
		                    Reader reader = new InputStreamReader(in, enc);
		                    BufferedReader bufferedReader = new BufferedReader(reader);
		                    String tinyUrl = bufferedReader.readLine();
		                    if (tinyUrl != null) {
		                    	if (mCompletedListener != null)
		                    		mCompletedListener.onSuccess(tinyUrl);
		                    } else {
		                        throw new IOException("empty response");
		                    }
		                } else {
		                    String errorTemplate = "unexpected response: %d";
		                    String msg = String.format(errorTemplate, statusCode);
		                    throw new IOException(msg);
		                }
		            } finally {
		                in.close();
		            }
		        } catch (IOException e) {
		        	if (mCompletedListener != null)
                		mCompletedListener.onFail(e, null);
		        }
			}
		});
		thread.start();
	}
}
