package com.ziggy.datalab.druid;

import org.apache.commons.lang3.StringUtils;

import com.ziggy.datalab.util.time.TimeUtil;

public class TimeseriesQuery extends Query
{
    private String       query;

    public TimeseriesQuery( String metric, String fDimension, String fValue, String granularity, String fromDate, String toDate )
    {
        // queries are quite hardcoded :(
        // but they are as flexible as expected for this 2-week-project
        String period = getGranularity(granularity);
        this.query = "{\"queryType\": \"timeseries\","
                     + "\"dataSource\": \"payments\","
                     + "\"granularity\": {\"type\": \"period\", \"period\": \"" + period + "\", \"timeZone\": \"Europe/Madrid\"},";
        if ( StringUtils.isNotBlank(fDimension) && StringUtils.isNotBlank(fValue) )
            query += "\"filter\":{\"type\":\"selector\", \"dimension\":\"" + fDimension + "\", \"value\":\"" + fValue + "\"},";
        query += "\"aggregations\":[" + getAgg(metric) + "],"
                 + "\"postAggregations\":[" + getPostAgg(metric) + "],"
                 + "\"intervals\": "
                 + "[\"" + TimeUtil.parseToISO(TimeUtil.parseToMillis(fromDate, "Europe/Madrid")) + "/"
                 + TimeUtil.parseToISO(TimeUtil.parseToMillis(toDate, "Europe/Madrid")) + "\"],"
                 + "\"context\":{\"skipEmptyBuckets\":\"true\","
                 + "\"timeout\":40000}}";
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

    public String getQuery()
    {
        return query;
    }

    public void setQuery( String query )
    {
        this.query = query;
    }

}
