package com.ziggy.datalab.api.handler;

import org.json.JSONArray;
import org.json.JSONObject;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class DefaultHandler implements HttpHandler
{
    @Override
    public void handleRequest( HttpServerExchange exchange ) throws Exception
    {
        if ( exchange.isInIoThread() )
        {
            exchange.dispatch(this);
            return;
        }
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(getBasicInfo());
    }
    
    private static String getBasicInfo()
    {
        JSONObject info = new JSONObject();
        JSONObject group = new JSONObject();
        group.put("name", "Ziggy Stardust");
        JSONArray members = new JSONArray();
        members.put("Federico Casanova");
        group.put("members", members);
        info.put("group", group);
        info.put("event", "Cajamar Datahack 2017");
        return info.toString();
    }
}
