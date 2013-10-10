package com.anees.solutionpuzzle;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class JsonParser {
	static JSONArray jArray;
	static JSONObject jObj;
	// <Generated Authorization Key>
	static final String oauth_key = "Bearer AAAAAAAAAAAAAAAAAAAAAH2TTgAAAAAA0pDT3i7w2c0r1fkkAxgkupuzhR4%3DYwj22QQEXS5INepTzmniEv9rcyfpUEZDb4oSlAOozA";

	public JsonParser() {

	}

	public JSONArray getJSONArrayFromURL(String url) {
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient(
					new BasicHttpParams());
			HttpGet httpget = new HttpGet(url);
			
			httpget.setHeader("Authorization", oauth_key);
			httpget.setHeader("Content-type", "application/json");

			InputStream inputStream = null;
			HttpResponse response = httpclient.execute(httpget);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			Log.w("Status Code", "" + statusCode);
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				inputStream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
//				Log.d("JSONArray", sb.toString());
				jArray = new JSONArray(sb.toString());
			}
		} catch (Exception e) {
			Log.e("Error JsonParsing JSONArray", e.getMessage());
		}
		return jArray;
	}

	public JSONObject getJsonObjectFromURL(String url) {
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient(
					new BasicHttpParams());
			HttpGet httpget = new HttpGet(url);
			httpget.setHeader("Authorization", oauth_key);
			httpget.setHeader("Content-type", "application/json");

			InputStream inputStream = null;
			HttpResponse response = httpclient.execute(httpget);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			Log.w("Status Code", "" + statusCode);
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				inputStream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
//				Log.d("JSONObject", sb.toString());
				jObj = new JSONObject(sb.toString());
			}
		} catch (Exception e) {
			Log.e("Error JsonParsing JsonObject", e.getMessage());
		}
		return jObj;
	}

}
