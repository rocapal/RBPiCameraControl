/*
 *
 *  Copyright (C) Roberto Calvo Palomino
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/. 
 *
 *  Author : Roberto Calvo Palomino <rocapal at gmail dot com>
 *
 */


package es.pentalo.apps.RBPiCameraControl.API;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;

public class RBPiCamera {

	private final String TAG = getClass().getSimpleName();
	
	private String mBaseUrl;
	
	public RBPiCamera (String baseUrl)
	{
		mBaseUrl = baseUrl;
	}
	
	private HttpEntity getEntityGet (String url)
	{
		
		try
		{
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpGet = null;

			httpGet = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpGet);
			
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{
				return response.getEntity();										
			}
			
			return null;

		}catch (IOException e) {
			Log.e(TAG,e.getMessage());
			return null;
		}	
	}
	private HttpEntity getEntityPost (String url, List<Command> commands)
	{
		
		
		Gson gson = new Gson();
		String json = gson.toJson(commands);
		
		Log.d(TAG, json);
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(url);
		StringEntity se;
		
		try {
			se = new StringEntity(json);
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage());			
			return null;
		}
		
		httpost.setEntity(se);
		httpost.setHeader("Accept", "application/json");
	    httpost.setHeader("Content-type", "application/json");
	    
	    
	    try {
			HttpResponse response =  httpclient.execute(httpost);
			
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{
				return response.getEntity(); 			
			}
			else
			{
				Log.w(TAG, "HTTP Response: " + String.valueOf(response.getStatusLine().getStatusCode()));
				return null;
			}
            
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage());
			return null;
			
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return null;
			
		}		
	}
	
	synchronized public Bitmap shotPhoto (List<Command> commands)
	{				
		HttpEntity entity = getEntityPost(mBaseUrl + "api/photo/shot/",commands);
		
		if (entity != null)
		{
			InputStream inputStream;
			try {
				inputStream = entity.getContent();
				return BitmapFactory.decodeStream(inputStream);						
				
			} catch (IllegalStateException e) {
				Log.e(TAG, e.getMessage());
				return null;
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
				return null;
			}			
		}
		
		return null;
	}
	
	synchronized public String startStreaming (List<Command> commands)
	{						
		try
		{
			HttpEntity entity = getEntityPost(mBaseUrl + "api/video/streaming/",commands);
			if (entity == null)
				return null;
			
			String str =  EntityUtils.toString(entity);
			return getStreamingURL(str);
			
	    } catch (ClientProtocolException e) {
	    	Log.e(TAG, e.getMessage());
	    	return null;

	    } catch (IOException e) {
	    	Log.e(TAG, e.getMessage());
	    	return null;

	    }			    	    
	}
	
	synchronized public Boolean stopStreaming ()
	{
		try
		{
			HttpEntity entity = getEntityGet(mBaseUrl + "api/video/streaming/stop/");
			if (entity == null)
				return false;
			
			String str =  EntityUtils.toString(entity);
			try {
				JSONObject json = new JSONObject(str);
				return (json.has("code") && json.getInt("code") == 200);
					
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
		    	return false;
			}
			
			
	    } catch (ClientProtocolException e) {
	    	Log.e(TAG, e.getMessage());
	    	return false;

	    } catch (IOException e) {
	    	Log.e(TAG, e.getMessage());
	    	return false;

	    }	
	}
	
	private String getStreamingURL (String jsonStr)
	{
		try {
			JSONObject json = new JSONObject(jsonStr);
			if (json.has("code") && json.getInt("code")==200)
				if (json.has("streaming_url"))
					return json.getString("streaming_url");
			
			return null;
				
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
}
