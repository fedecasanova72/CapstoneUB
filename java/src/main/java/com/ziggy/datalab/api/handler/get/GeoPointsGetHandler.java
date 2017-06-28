package com.ziggy.datalab.api.handler.get;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ziggy.datalab.druid.GeoPointsQuery;
import com.ziggy.datalab.util.http.HttpUtil;
import com.ziggy.datalab.util.http.UnirestHttpRequest;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

public class GeoPointsGetHandler implements HttpHandler
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

        // create druid query
        GeoPointsQuery query = new GeoPointsQuery(metric, fromDate, toDate);
        System.out.println(query.getQuery());
        // get response and parse it to get the result (as echarts expects it)
        String resp = UnirestHttpRequest.post("http://www.ziggy.ovh:8082/druid/v2/?pretty", query.getQuery());

        JSONArray jArray = new JSONArray(resp);
        List<List<Number>> geoData = new ArrayList<>();
        List<List<Number>> geoData2 = new ArrayList<>();
        List<List<Number>> geoData3 = new ArrayList<>();
        for ( int i = 0; i < jArray.length(); i++ )
        {
            JSONObject jObject = jArray.getJSONObject(i);
            List<Number> inner = new ArrayList<>();
            inner.add(jObject.getJSONObject("event").getDouble("LON_CLIENTE"));
            inner.add(jObject.getJSONObject("event").getDouble("LAT_CLIENTE"));
            // points are put in three arrays
            // because in the resulting map there are 2 colors
            // blue for all data, skyblue for data with more than 200 operations
            // and white data for more than 480 points in the period
            // that gives a very nice visual effect (see in web)
            if ( jObject.getJSONObject("event").getDouble("count") > 200 )
                geoData2.add(inner);
            if ( jObject.getJSONObject("event").getDouble("count") > 480 )
                geoData3.add(inner);
            geoData.add(inner);
        }

        JSONObject jObj = new JSONObject();
        jObj.put("geoPoints", geoData);
        jObj.put("geoPointsMid", geoData2);
        jObj.put("geoPointsBig", geoData3);

        String origin = exchange.getRequestHeaders().getFirst(Headers.ORIGIN);

        // response
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), HttpUtil.getAllowedOrigin(origin));
        exchange.getResponseSender().send(jObj.toString());
    }
}
