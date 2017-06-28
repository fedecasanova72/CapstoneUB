package com.ziggy.datalab.api.handler.get;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class FaviconGetHandler implements HttpHandler
{
	@Override
	public void handleRequest( HttpServerExchange exchange ) throws Exception
	{
		if ( exchange.isInIoThread() )
		{
			// send to worker thread
			exchange.dispatch(this);
			return;
		}

		// already in worker thread...
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
		exchange.getResponseSender().send(new String());
	}
}
