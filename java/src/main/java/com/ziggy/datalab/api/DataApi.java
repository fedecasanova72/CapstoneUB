package com.ziggy.datalab.api;

import com.ziggy.datalab.api.handler.RestRouting;

import io.undertow.Undertow;

public class DataApi
{
	private static final String	HOST			= "localhost";
	private static final int	PORT			= 1947;
	private static final int	WORKER_THREADS	= 32;

	public static void main( final String[] args )
	{
		initApi();
	}

	private static void initApi()
	{
	    // undertow is the API used
		RestRouting routing = new RestRouting();
		Undertow server = Undertow.builder()
				.addHttpListener(PORT, HOST)
				.setHandler(routing)
				.setWorkerThreads(WORKER_THREADS)
				.build();
		server.start();
	}

}
