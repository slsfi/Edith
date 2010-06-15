package fi.finlit.edith.domain;

import org.joda.time.DateTime;
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
    
    private static final long serialVersionUID = 6320083216375055746L;

    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("d.M.y");

    @Id(IDType.LOCAL)
    private String id;

    @Predicate
    private DateTime start;
    
    @Predicate
    private DateTime end;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id){
        this.id = id;
    }
    
    public Interval() {}

    public Interval(DateTime start, DateTime end) {
        this.start = start;
        this.end = end;
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
        return new Interval(new DateTime(year, 1, 1, 0, 0, 0, 0), new DateTime(year + 1, 1, 1, 0, 0, 0, 0));
    }

    public static Interval createDate(LocalDate localDate) {
        return Interval.createDate(localDate.toDateTimeAtStartOfDay());
    }

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
