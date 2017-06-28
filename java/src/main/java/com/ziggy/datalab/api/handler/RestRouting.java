package com.ziggy.datalab.api.handler;

import com.ziggy.datalab.api.handler.get.DataGetHandler;
import com.ziggy.datalab.api.handler.get.DimensionsGetHandler;
import com.ziggy.datalab.api.handler.get.FaviconGetHandler;
import com.ziggy.datalab.api.handler.get.GeoGetHandler;
import com.ziggy.datalab.api.handler.get.GeoLinesGetHandler;
import com.ziggy.datalab.api.handler.get.GeoPointsGetHandler;
import com.ziggy.datalab.api.handler.get.RiverGetHandler;
import com.ziggy.datalab.api.handler.get.TopGetHandler;

import io.undertow.server.RoutingHandler;

public class RestRouting extends RoutingHandler
{
    public RestRouting()
    {
        // there's one handler defined for each mount point
        super();
        this.get("/data", new DataGetHandler());
        this.get("/top", new TopGetHandler());
        this.get("/geo", new GeoGetHandler());
        this.get("/river", new RiverGetHandler());
        this.get("/geolines", new GeoLinesGetHandler());
        this.get("/geopoints", new GeoPointsGetHandler());
        this.get("/dimensions", new DimensionsGetHandler());
        this.get("/favicon.ico", new FaviconGetHandler());
        this.setFallbackHandler(new DefaultHandler());
    }
}
