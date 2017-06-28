package com.ziggy.datalab.api.handler.get;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ziggy.datalab.druid.GeoLinesQuery;
import com.ziggy.datalab.util.http.HttpUtil;
import com.ziggy.datalab.util.http.UnirestHttpRequest;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

public class GeoLinesGetHandler implements HttpHandler
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

        // get query params
        Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();
        String metric = queryParameters.get("metric").getFirst();
        String fromDate = queryParameters.get("fromDate").getFirst();
        String toDate = queryParameters.get("toDate").getFirst();

        GeoLinesQuery query = new GeoLinesQuery(metric, fromDate, toDate);
        System.out.println(query.getQuery());
        String resp = UnirestHttpRequest.post("http://www.ziggy.ovh:8082/druid/v2/?pretty", query.getQuery());

        JSONArray jArray = new JSONArray(resp);
        List<List<List<Number>>> geoData = new ArrayList<>();
        for ( int i = 0; i < jArray.length(); i++ )
        {
            JSONObject jObject = jArray.getJSONObject(i);
            List<List<Number>> inner = new ArrayList<>();
            List<Number> inner1 = new ArrayList<>();
            List<Number> inner2 = new ArrayList<>();
            inner1.add(jObject.getJSONObject("event").getDouble("LON_CLIENTE"));
            inner1.add(jObject.getJSONObject("event").getDouble("LAT_CLIENTE"));
            inner2.add(jObject.getJSONObject("event").getDouble("LON_COMERCIO"));
            inner2.add(jObject.getJSONObject("event").getDouble("LAT_COMERCIO"));
            inner.add(inner1);
            inner.add(inner2);
            geoData.add(inner);
        }

        JSONObject jObj = new JSONObject();
        jObj.put("geoLines", geoData);

        String origin = exchange.getRequestHeaders().getFirst(Headers.ORIGIN);

        // response
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), HttpUtil.getAllowedOrigin(origin));
        exchange.getResponseSender().send(jObj.toString());
    }
}
