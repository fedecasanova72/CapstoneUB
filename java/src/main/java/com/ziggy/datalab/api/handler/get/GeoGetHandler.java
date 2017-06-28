package com.ziggy.datalab.api.handler.get;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ziggy.datalab.druid.GeoQuery;
import com.ziggy.datalab.util.http.HttpUtil;
import com.ziggy.datalab.util.http.UnirestHttpRequest;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

public class GeoGetHandler implements HttpHandler
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
        String type = queryParameters.get("type").getFirst();
        String fromDate = queryParameters.get("fromDate").getFirst();
        String toDate = queryParameters.get("toDate").getFirst();
        String fDimension = queryParameters.get("fDimension").getFirst();
        String fValue = queryParameters.get("fValue").getFirst();

        // create druid query
        GeoQuery query = new GeoQuery(metric, type, fromDate, toDate, fDimension, fValue);
        System.out.println(query.getQuery());
        // get response and parse it to get the result (as echarts expects it)
        String resp = UnirestHttpRequest.post("http://www.ziggy.ovh:8082/druid/v2/?pretty", query.getQuery());

        JSONArray jArray = new JSONArray(resp);
        List<JSONObject> geoData = new ArrayList<>();
        List<Double> metricValues = new ArrayList<>();
        for ( int i = 0; i < jArray.length(); i++ )
        {
            JSONObject jObject = jArray.getJSONObject(i);
            List<Number> inner = new ArrayList<>();
            JSONObject geoValue = new JSONObject();
            String name = jObject.getJSONObject("event").getString("CIUDAD_" + type.toUpperCase());
            inner.add(jObject.getJSONObject("event").getDouble("LON_" + type.toUpperCase()));
            inner.add(jObject.getJSONObject("event").getDouble("LAT_" + type.toUpperCase()));
            inner.add(jObject.getJSONObject("event").getDouble(metric));
            metricValues.add(jObject.getJSONObject("event").getDouble(metric));
            geoValue.put("name", name);
            geoValue.put("value", inner);
            geoData.add(geoValue);
        }
        double mean = mean(metricValues);
        double sd = sd(metricValues);

        JSONObject jObj = new JSONObject();
        jObj.put("geoData", geoData);
        jObj.put("max", mean + 2 * sd);

        String origin = exchange.getRequestHeaders().getFirst(Headers.ORIGIN);

        // response
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), HttpUtil.getAllowedOrigin(origin));
        exchange.getResponseSender().send(jObj.toString());
    }

    private double sd( List<Double> a )
    {
        if ( a.size() == 1 )
            return 0;
        int sum = 0;
        double mean = mean(a);
        for ( Double i : a )
            sum += Math.pow((i - mean), 2);
        return Math.sqrt(sum / (a.size() - 1));
    }

    private double mean( List<Double> list )
    {
        return list.stream().reduce(0d, Double::sum) / list.size();
    }

}
