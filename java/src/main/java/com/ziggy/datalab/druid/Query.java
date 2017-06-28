package com.ziggy.datalab.druid;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public class Query
{
    // the metrics that will be needed for the API are defined here
    // in a json-druid way
    
    protected final String sumImporte     = "{\"type\":\"doubleSum\", \"name\":\"total_importe\", \"fieldName\":\"IMPORTE\"}";
    protected final String sumNumOps      = "{\"type\":\"doubleSum\", \"name\":\"total_num_op\", \"fieldName\":\"NUM_OP\"}";
    protected final String sumTemperatura = "{\"type\":\"doubleSum\", \"name\":\"sum_TEMPERATURA\", \"fieldName\":\"TEMPERATURA\"}";
    protected final String sumHumedad     = "{\"type\":\"doubleSum\", \"name\":\"sum_HUMEDAD\", \"fieldName\":\"HUMEDAD\"}";
    protected final String count          = "{\"type\":\"count\", \"name\":\"count\"}";
    protected String       division       = "{\"type\":\"arithmetic\",\"name\":\"%NAME%\",\"fn\":\"/\",\"fields\":[%FIELDS%]}";
    protected String       field          = "{\"type\":\"fieldAccess\",\"fieldName\":\"%NAME%\"}";

    protected String getAgg( String metric )
    {
        switch ( metric )
        {
            case "total_num_op":
                return sumNumOps;
            case "avg_num_op":
                return StringUtils.join(Arrays.asList(sumNumOps, count), ",");
            case "total_importe":
                return sumImporte;
            case "avg_importe":
                return StringUtils.join(Arrays.asList(sumImporte, sumNumOps), ",");
            case "count":
                return count;
            case "avg_temperatura":
                return StringUtils.join(Arrays.asList(sumTemperatura, count), ",");
            default:
                return null;
        }
    }

    protected String getPostAgg( String metric )
    {
        String field1;
        String field2;
        switch ( metric )
        {
            case "avg_num_op":
                field1 = field.replaceAll("%NAME%", "total_num_op");
                field2 = field.replaceAll("%NAME%", "count");
                return division.replaceAll("%NAME%", metric).replaceAll("%FIELDS%", StringUtils.join(Arrays.asList(field1, field2), ","));
            case "avg_importe":
                field1 = field.replaceAll("%NAME%", "total_importe");
                field2 = field.replaceAll("%NAME%", "total_num_op");
                return division.replaceAll("%NAME%", metric).replaceAll("%FIELDS%", StringUtils.join(Arrays.asList(field1, field2), ","));
            case "avg_temperatura":
                field1 = field.replaceAll("%NAME%", "sum_TEMPERATURA");
                field2 = field.replaceAll("%NAME%", "count");
                return division.replaceAll("%NAME%", metric).replaceAll("%FIELDS%", StringUtils.join(Arrays.asList(field1, field2), ","));
            case "total_importe":
            case "total_num_op":
            case "count":
                return "";
            default:
                return null;
        }
    }
}
