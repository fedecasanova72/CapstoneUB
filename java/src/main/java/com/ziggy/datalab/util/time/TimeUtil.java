package com.ziggy.datalab.util.time;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.ISODateTimeFormat;

public class TimeUtil
{
    // convert iso to millis and other cool stuff with time objects
    
    private static DateTimeParser[]  parsers   = { DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").getParser(),
            DateTimeFormat.forPattern("yyyy-MM-dd").getParser() };

    private static DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();

    public static long parseToMillis( String date, String timezone )
    {
        return formatter.withZone(DateTimeZone.forID(timezone)).parseDateTime(date).getMillis();
    }

    public static String parseToISO( long millis, String timezone )
    {
        return ISODateTimeFormat.dateTime().withZone(DateTimeZone.forID(timezone)).print(millis);
    }

    public static String parseToISO( long millis )
    {
        return parseToISO(millis, "UTC");
    }

    public static long ISOToMillis( String iso )
    {
        return new DateTime(iso).getMillis();
    }
    
    public static long convertToUtc( long timestamp, String timezone )
    {
        return timestamp - DateTimeZone.forID(timezone).getOffset(timestamp);
    }
}
