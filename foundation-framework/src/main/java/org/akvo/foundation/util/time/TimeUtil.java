package org.akvo.foundation.util.time;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

public final class TimeUtil {
    public LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.from(date.toInstant());
    }

    public LocalDateTime toLocalDateTime(Calendar calendar) {
        return toLocalDateTime(calendar.getTime());
    }

    public Date toDate(Calendar calendar) {
        return calendar.getTime();
    }

    public Date toDate(LocalDateTime localDateTime) {
        return new Date(localDateTime.toEpochSecond(ZoneOffset.UTC));
    }

    public Calendar toCalender(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return instance;
    }

    public Calendar toCalender(LocalDateTime localDateTime) {
        return toCalender(toDate(localDateTime));
    }

}
