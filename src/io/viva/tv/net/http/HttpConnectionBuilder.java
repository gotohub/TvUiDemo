package io.viva.tv.net.http;

import io.viva.tv.net.exception.DataErrorEnum;
import io.viva.tv.net.exception.DataException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.text.TextUtils;

public final class HttpConnectionBuilder {
	private String url;
	private String method;
	private int connectionTimeout;
	private int readTimeout;
	private boolean useCache;
	private boolean doOutput;
	private Map<String, String> properties;

	public HttpConnectionBuilder(String paramString1, String paramString2) {
		this.url = paramString1;
		this.method = paramString2;
		this.connectionTimeout = 5000;
		this.readTimeout = 10000;
		this.useCache = false;
		this.doOutput = false;
		this.properties = new HashMap();
		if (TextUtils.equals(paramString2, "POST")) {
			this.properties.put("Content-Type", "application/x-www-form-urlencoded");
			this.properties.put("Accept", "*/*");
			this.properties.put("Accept-Charset", "UTF8");
			this.properties.put("Connection", "Keep-Alive");
			this.properties.put("Cache-Control", "no-cache");
			this.doOutput = true;
		}
		System.setProperty("http.keepAlive", "false");
	}

	public HttpURLConnection build() throws DataException {
		try {
			HttpURLConnection localHttpURLConnection = (HttpURLConnection) new URL(this.url).openConnection();
			localHttpURLConnection.setRequestMethod(this.method);
			localHttpURLConnection.setConnectTimeout(this.connectionTimeout);
			localHttpURLConnection.setReadTimeout(this.readTimeout);
			localHttpURLConnection.setUseCaches(this.useCache);
			localHttpURLConnection.setDoOutput(this.doOutput);
			if (TextUtils.equals(this.method, "POST")) {
				int i = Integer.parseInt((String) this.properties.get("Content-Length"));
				localHttpURLConnection.setFixedLengthStreamingMode(i);
			}
			Set localSet = this.properties.entrySet();
			if (localSet != null) {
				Iterator localIterator = localSet.iterator();
				while (localIterator.hasNext()) {
					Map.Entry localEntry = (Map.Entry) localIterator.next();
					localHttpURLConnection.addRequestProperty((String) localEntry.getKey(), (String) localEntry.getValue());
				}
			}
			return localHttpURLConnection;
		} catch (Exception localException) {
		}
		throw new DataException(DataErrorEnum.CREATE_CONNECTION_FAILED);
	}

	public HttpConnectionBuilder setUrl(String paramString) {
		this.url = paramString;
		return this;
	}

	public HttpConnectionBuilder setMethod(String paramString) {
		this.method = paramString;
		return this;
	}

	public HttpConnectionBuilder setConnectionTimeout(int paramInt) {
		this.connectionTimeout = paramInt;
		return this;
	}

	public HttpConnectionBuilder setReadTimeout(int paramInt) {
		this.readTimeout = paramInt;
		return this;
	}

	public HttpConnectionBuilder setUseCache(boolean paramBoolean) {
		this.useCache = paramBoolean;
		return this;
	}

	public HttpConnectionBuilder setDoOutput(boolean paramBoolean) {
		this.doOutput = paramBoolean;
		return this;
	}

	public HttpConnectionBuilder setProperty(String paramString1, String paramString2) {
		this.properties.put(paramString1, paramString2);
		return this;
	}
}