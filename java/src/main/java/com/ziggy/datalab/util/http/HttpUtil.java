package com.ziggy.datalab.util.http;

import java.util.regex.Pattern;

public class HttpUtil
{
	public static String getAllowedOrigin( String origin )
	{
	    // for CORS
		String defaultOrigin = "http://ziggy.ovh";

		if ( origin == null )
			return defaultOrigin;
		
		// if the host that sends the request is from ziggy.ovh, this return the exact same
		// hostname as http header, so there's no cross-origin problem
		Pattern hostAllowedPattern = Pattern.compile("^(http(s)*:\\/\\/)*([a-zA-Z0-9]+\\.)ziggy\\.ovh");
		// Verify host
		if ( hostAllowedPattern.matcher(origin).matches() )
			return origin;
		else
			return defaultOrigin;
	}
}
