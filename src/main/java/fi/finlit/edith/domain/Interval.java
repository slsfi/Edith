package fi.finlit.edith.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.base.BaseInterval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.InjectProperty;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

@ClassMapping(ns = EDITH.NS)
public class Interval extends BaseInterval {
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("d.M.y");

    public Interval(@InjectProperty("start") DateTime start, @InjectProperty("end") DateTime end) {
        super(start, end);
    }

    @Predicate
    @Override
    public DateTime getStart() {
        return super.getStart();
    }

    @Predicate
    @Override
    public DateTime getEnd() {
        return super.getEnd();
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
