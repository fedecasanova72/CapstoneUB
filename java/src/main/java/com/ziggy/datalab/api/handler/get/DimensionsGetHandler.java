package com.ziggy.datalab.api.handler.get;

import org.json.JSONObject;

import com.ziggy.datalab.util.http.HttpUtil;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

public class DimensionsGetHandler implements HttpHandler
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

        // dimensions that our dataset accepts
        JSONObject jObj = new JSONObject();
        String[] dimensions = { "SECTOR", "FRANJA_HORARIA", "CIUDAD_CLIENTE", 
                "CIUDAD_COMERCIO", "TIEMPO_DESC", "DIA_SEMANA" };
        jObj.put("dimensions", dimensions);

        String origin = exchange.getRequestHeaders().getFirst(Headers.ORIGIN);

        // response
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), HttpUtil.getAllowedOrigin(origin));
        exchange.getResponseSender().send(jObj.toString());
    }
}
