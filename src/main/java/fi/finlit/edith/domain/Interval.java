package fi.finlit.edith.domain;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Id;
import com.mysema.rdfbean.annotations.Predicate;
import com.mysema.rdfbean.model.IDType;

import fi.finlit.edith.EDITH;

@ClassMapping(ns = EDITH.NS)
public class Interval {
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("d.M.y").withZone(
            DateTimeZone.UTC);

    @Id(IDType.LOCAL)
    private String id;

    @Predicate
    private DateTime start;

    @Predicate
    private DateTime end;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id == null ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Interval other = (Interval) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

}
