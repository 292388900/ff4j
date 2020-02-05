package org.ff4j.core.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/*
 * #%L
 * ff4j-core
 * %%
 * Copyright (C) 2013 - 2016 FF4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.util.Calendar;
import java.util.Date;

/**
 * Utilities methods about, time & dates
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class TimeUtils {
    
    /** Formatting date. */
    public static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    /** Formatting date. */
    public static final DateTimeFormatter FORMATTER_DATETIME = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
	/**
	 * Hide default constructor.
	 */
	private TimeUtils() {
	}
	
	public static String formatDay(LocalDateTime ldt) {
	    if (null == ldt) return null; 
	    return FORMATTER_DATE.format(ldt);
	}
	
	public static LocalDateTime parseDay(String day) {
        if (null == day) return null; 
        return (LocalDateTime) FORMATTER_DATE.parse(day);
    }
	
	public static String formatDateTime(LocalDateTime ldt) {
        if (null == ldt) return null; 
        return FORMATTER_DATETIME.format(ldt);
    }
	
	public static LocalDateTime parseDateTime(String dateTime) {
        if (null == dateTime) return null; 
        return (LocalDateTime) FORMATTER_DATETIME.parse(dateTime);
    }

	/**
	 * Compute time for today midnight.
	 *
	 * @return
	 * 		today at midnight
	 */
	public static long getTodayMidnightTime() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTimeInMillis();
	}

	/**
	 * Compute time for tomorrow midnight.
	 *
	 * @return
	 * 		tomorrow at midnight
	 */
	public static long getTomorrowMidnightTime() {
		Calendar c2 = Calendar.getInstance();
		c2.setTime(new Date(System.currentTimeMillis() + 1000 * 3600 * 24));
		c2.set(Calendar.HOUR_OF_DAY, 0);
		c2.set(Calendar.MINUTE, 0);
		c2.set(Calendar.SECOND, 0);
		return c2.getTimeInMillis();
	}
	
	public static LocalDateTime asLocalDateTime(java.sql.Timestamp sqlTimeStamp) {
	    if (sqlTimeStamp == null) return null;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(sqlTimeStamp.getTime()), ZoneId.systemDefault());
    }
	
	public static LocalDateTime asLocalDateTime(Instant instant) {
	    if (instant == null) return null;
	    return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
	}
	
	public static Instant toInstant(LocalDateTime ldt) {
        if (ldt == null) return null;
        return ldt.toInstant(ZoneOffset.UTC);
    }
    
    public static java.sql.Timestamp asSqlTimeStamp(LocalDateTime jdk8Date) {
        if (jdk8Date == null) return null;
        ZoneOffset zof = ZoneId.systemDefault().getRules().getOffset(jdk8Date);
        return new java.sql.Timestamp(jdk8Date.toEpochSecond(zof));
    }

}
