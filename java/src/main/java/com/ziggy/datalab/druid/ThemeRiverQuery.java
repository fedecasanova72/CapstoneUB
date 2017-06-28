package com.ziggy.datalab.druid;

import org.apache.commons.lang3.StringUtils;

import com.ziggy.datalab.util.time.TimeUtil;

public class ThemeRiverQuery extends Query
{
    private String query;

    public ThemeRiverQuery( String metric, String dimension, String granularity, String fromDate, String toDate, String fDimension, String fValue )
    {
        String period = getGranularity(granularity);
        this.query = " {\"queryType\": \"groupBy\","
                     + "\"dataSource\": \"payments\","
                     + "\"granularity\": {\"type\": \"period\", \"period\": \"" + period + "\", \"timeZone\": \"Europe/Madrid\"},"
                     + "\"dimensions\": [\"" + dimension + "\"],"
                     + "\"metrics\": [],"
                     + "\"intervals\": "
                     + "[\"" + TimeUtil.parseToISO(TimeUtil.parseToMillis(fromDate, "Europe/Madrid")) + "/"
                     + TimeUtil.parseToISO(TimeUtil.parseToMillis(toDate, "Europe/Madrid")) + "\"],";
        if ( StringUtils.isNotBlank(fDimension) && StringUtils.isNotBlank(fValue) )
            query += "\"filter\":{\"type\":\"selector\", \"dimension\":\"" + fDimension + "\", \"value\":\"" + fValue + "\"},";
        query += "\"aggregations\":[" + getAgg(metric) + "],"
                 + "\"postAggregations\":[" + getPostAgg(metric) + "],"
                 + "\"limitSpec\":{\"type\":\"default\",\"columns\":[]},"
                 + "\"context\":{\"timeout\":40000}}";
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery( String query )
    {
        this.query = query;
    }

    private String getGranularity( String granularity )
    {
        switch ( granularity.toLowerCase() )
        {
            case "hour":
                return "P1H";
            case "day":
                return "P1D";
            case "week":
                return "P1W";
            case "month":
                return "P1M";
            default:
                return null;
        }
    }
}
