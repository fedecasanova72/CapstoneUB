package com.ziggy.datalab.druid;

import org.apache.commons.lang3.StringUtils;

import com.ziggy.datalab.util.time.TimeUtil;

public class TopNQuery extends Query
{
    private String query;

    public TopNQuery( String metric, String dimension, String sLimit, String fromDate, String toDate, String fDimension, String fValue )
    {
        int limit = 100;
        if ( StringUtils.isNotBlank(sLimit) )
            limit = Integer.parseInt(sLimit);
        this.query = " {\"queryType\": \"topN\","
                     + "\"dataSource\": \"payments\","
                     + "\"granularity\": \"all\","
                     + "\"dimension\": \"" + dimension + "\","
                     + "\"metric\": \"" + metric + "\","
                     + "\"intervals\": "
                     + "[\"" + TimeUtil.parseToISO(TimeUtil.parseToMillis(fromDate, "Europe/Madrid")) + "/"
                     + TimeUtil.parseToISO(TimeUtil.parseToMillis(toDate, "Europe/Madrid")) + "\"],";
        if ( StringUtils.isNotBlank(fDimension) && StringUtils.isNotBlank(fValue) )
            query += "\"filter\":{\"type\":\"selector\", \"dimension\":\"" + fDimension + "\", \"value\":\"" + fValue + "\"},";
        query += "\"aggregations\":[" + getAgg(metric) + "],"
                 + "\"postAggregations\":[" + getPostAgg(metric) + "],"
                 + "\"threshold\": " + limit + ","
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

}
