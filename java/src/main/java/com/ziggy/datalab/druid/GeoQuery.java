package com.ziggy.datalab.druid;

import org.apache.commons.lang3.StringUtils;

import com.ziggy.datalab.util.time.TimeUtil;

public class GeoQuery extends Query
{
    private String query;

    public GeoQuery( String metric, String type, String fromDate, String toDate, String fDimension, String fValue )
    {
        String dimensions = "\"dimensions\":[\"CIUDAD_%TYPE%\", \"CP_%TYPE%\", \"LAT_%TYPE%\", \"LON_%TYPE%\"]";
        if ( type.equalsIgnoreCase("cliente") )
            dimensions = dimensions.replace("%TYPE%", "CLIENTE");
        else if ( type.equalsIgnoreCase("comercio") )
            dimensions = dimensions.replace("%TYPE%", "COMERCIO");
        this.query = "{\"queryType\": \"groupBy\","
                     + "\"dataSource\": \"payments\","
                     + "\"granularity\": \"all\","
                     + dimensions + ","
                     + "\"aggregations\":[" + getAgg(metric) + "],"
                     + "\"postAggregations\":[" + getPostAgg(metric) + "],"
                     + "\"intervals\": "
                     + "[\"" + TimeUtil.parseToISO(TimeUtil.parseToMillis(fromDate, "Europe/Madrid")) + "/"
                     + TimeUtil.parseToISO(TimeUtil.parseToMillis(toDate, "Europe/Madrid")) + "\"],";
        if ( StringUtils.isNotBlank(fDimension) && StringUtils.isNotBlank(fValue) )
            query += "\"filter\":{\"type\":\"selector\", \"dimension\":\"" + fDimension + "\", \"value\":\"" + fValue + "\"},";
        query += "\"context\":{\"skipEmptyBuckets\":\"true\","
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
