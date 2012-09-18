package com.mysema.edith.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@Embeddable
public class Interval {
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("d.M.y").withZone(
            DateTimeZone.UTC);

    @Column(name = "start_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime start;

    @Column(name = "end_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime end;

    public Interval() {
    }

    public Interval(DateTime start, DateTime end) {
        this.start = start.withZoneRetainFields(DateTimeZone.UTC);
        this.end = end.withZoneRetainFields(DateTimeZone.UTC);
    }

    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public DateTime getEnd() {
        return end;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    public boolean isDate() {
        return getStart().equals(getEnd());
    }

    public boolean isYear() {
        // FIXME Naive, doesn't consider ranges yet.
        return !isDate();
    }

    public int getYear() {
        return getStart().getYear();
    }

    public LocalDate getDate() {
        return getStart().toLocalDate();
    }

    public static Interval createYear(int year) {
        return new Interval(new DateTime(year, 1, 1, 0, 0, 0, 0), new DateTime(year + 1, 1, 1, 0,
                0, 0, 0));
    }

    /**
     * This creates an empty interval for the start of the given date
     * 
     * @param localDate
     * @return
     */
    public static Interval createDate(LocalDate localDate) {
        return Interval.createDate(localDate.toDateTimeAtStartOfDay());
    }

    /**
     * This creates an empty interval for the given timestamp
     * 
     * @param dateTime
     * @return
     */
    public static Interval createDate(DateTime dateTime) {
        return new Interval(dateTime, dateTime);
    }

    public String asString() {
        if (isYear()) {
            return String.valueOf(getYear());
        }
        return formatter.print(getDate());
    }

    public static Interval fromString(String s) {
        try {
            return Interval.createDate(formatter.parseDateTime(s));
        } catch (IllegalArgumentException e) {
            return Interval.createYear(Integer.parseInt(s));
        }
    }

}
