package com.ziggy.datalab.api.handler.get;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ziggy.datalab.druid.TimeseriesQuery;
import com.ziggy.datalab.util.http.HttpUtil;
import com.ziggy.datalab.util.http.UnirestHttpRequest;
import com.ziggy.datalab.util.time.TimeUtil;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

public class DataGetHandler implements HttpHandler
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
        String fDimension = queryParameters.get("fDimension").getFirst();
        String fValue = queryParameters.get("fValue").getFirst();
        String granularity = queryParameters.get("granularity").getFirst();
        String fromDate = queryParameters.get("fromDate").getFirst();
        String toDate = queryParameters.get("toDate").getFirst();
        
        // create druid query
        TimeseriesQuery query = new TimeseriesQuery(metric, fDimension, fValue, granularity, fromDate, toDate);
        System.out.println(query.getQuery());
        // get response and parse it to get the result (as echarts expects it)
        String resp = UnirestHttpRequest.post("http://www.ziggy.ovh:8082/druid/v2/?pretty", query.getQuery());
        
        JSONArray jArray = new JSONArray(resp);
        List<List<Number>> timestamps = new ArrayList<>();
        for ( int i = 0; i < jArray.length(); i++ )
        {
            JSONObject jObject = jArray.getJSONObject(i);
            List<Number> inner = new ArrayList<>();
            inner.add(TimeUtil.ISOToMillis(jObject.getString("timestamp")));
            inner.add(jObject.getJSONObject("result").getDouble(metric));
            timestamps.add(inner);
        }

        JSONObject jObj = new JSONObject();
        jObj.put("timestamps", timestamps);

        String origin = exchange.getRequestHeaders().getFirst(Headers.ORIGIN);

        // response
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), HttpUtil.getAllowedOrigin(origin));
        exchange.getResponseSender().send(jObj.toString());
    }

}
