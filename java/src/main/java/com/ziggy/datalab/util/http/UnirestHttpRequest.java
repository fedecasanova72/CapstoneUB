package com.ziggy.datalab.util.http;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class UnirestHttpRequest
{
	public static String post( String targetURL, String postMessage )
	{
	    // fastest HTTP requests!
		try
		{
			HttpResponse<String> jsonResponse = Unirest.post(targetURL)
					.header("Content-Type", "application/json")
					.header("charset", "utf-8")
					.header("cache-control", "no-cache")
					.header("accept", "application/json")
					.body(postMessage)
					.asString();

			return jsonResponse.getBody();
		}
		catch ( UnirestException e )
		{
			e.printStackTrace();
			return null;
		}
	}

	public static String get( String targetURL )
	{
		try
		{
			HttpResponse<String> jsonResponse = Unirest.get(targetURL)
					.header("Content-Type", "application/json")
					.header("charset", "utf-8")
					.asString();

			return jsonResponse.getBody();
		}
		catch ( UnirestException e )
		{
			e.printStackTrace();
			return null;
		}
	}
}