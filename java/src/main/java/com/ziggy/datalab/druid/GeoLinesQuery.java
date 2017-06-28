package com.ziggy.datalab.druid;

import com.ziggy.datalab.util.time.TimeUtil;

public class GeoLinesQuery extends Query
{
    private String query;

    public GeoLinesQuery( String metric, String fromDate, String toDate )
    {
        String dimensions = "\"dimensions\":[\"LAT_CLIENTE\", \"LON_CLIENTE\", \"LAT_COMERCIO\", \"LON_COMERCIO\"]";
        this.query = "{\"queryType\": \"groupBy\","
                     + "\"dataSource\": \"payments\","
                     + "\"granularity\": \"all\","
                     + dimensions + ","
                     + "\"aggregations\":[" + getAgg(metric) + "],"
                     + "\"postAggregations\":[" + getPostAgg(metric) + "],"
                     + "\"intervals\": "
                     + "[\"" + TimeUtil.parseToISO(TimeUtil.parseToMillis(fromDate, "Europe/Madrid")) + "/"
                     + TimeUtil.parseToISO(TimeUtil.parseToMillis(toDate, "Europe/Madrid")) + "\"],"
                     + "\"context\":{\"skipEmptyBuckets\":\"true\","
                     + "\"limitSpec\":{\"type\":\"default\",\"columns\":[]},"
                     + "\"timeout\":40000}}";
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery( String query )
    {
        this.query = query;
    }

}
