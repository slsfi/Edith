package fi.finlit.edith.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

public class IntervalTest {
    @Test
    public void Start_Equals_End_When_Specific_Date() {
        LocalDate localDate = new LocalDate(2010, 1, 1);
        Interval interval = Interval.createDate(localDate);
        assertEquals(localDate, interval.getStart().toLocalDate());
        assertEquals(localDate, interval.getEnd().toLocalDate());
        assertEquals(interval.getStart(), interval.getEnd());
    }

    @Test
    public void Start_Is_First_Of_Given_Year_And_End_Is_Last_When_Specific_Year() {
        Interval interval = Interval.createYear(2002);
        assertEquals(new DateTime(2002, 1, 1, 0, 0, 0, 0), interval.getStart());
        assertEquals(new DateTime(2003, 1, 1, 0, 0, 0, 0), interval.getEnd());
    }

    @Test
    public void Is_Date_Is_True_When_Start_And_End_Are_Equal() {
        LocalDate localDate = new LocalDate(2010, 1, 1);
        assertTrue(Interval.createDate(localDate).isDate());
    }

    @Test
    public void Is_Date_Is_False_When_Start_And_End_Are_Not_Equal() {
        assertFalse(Interval.createYear(2010).isDate());
    }

    @Test
    public void Is_Year_Is_True_When_Start_And_End_Are_Not_Equal() {
        assertTrue(Interval.createYear(1998).isYear());
    }

    @Test
    public void Is_Year_Is_False_When_Start_And_End_Are_Equal() {
        LocalDate localDate = new LocalDate(2010, 1, 1);
        assertFalse(Interval.createDate(localDate).isYear());
    }

    @Test
    public void Get_Year() {
        assertEquals(1998, Interval.createYear(1998).getYear());
    }

    @Test
    public void Get_Date() {
        LocalDate localDate = new LocalDate(2010, 1, 1);
        assertEquals(localDate, Interval.createDate(localDate).getDate());
    }

    @Test
    public void As_String_Year() {
        assertEquals("2010", Interval.createYear(2010).asString());
    }

    @Test
    public void As_String_Date() {
        assertEquals("1.1.201", Interval.createDate(new LocalDate(201, 1, 1)).asString());
    }

    @Test
    public void As_String_Date_Several_Digits() {
        assertEquals("24.10.2010", Interval.createDate(new LocalDate(2010, 10, 24)).asString());
    }

    @Test
    public void Date_Interval_From_String() {
        assertEquals(new LocalDate(201, 1, 2),  Interval.fromString("2.1.201").getDate());
    }

    @Test
    public void Date_Interval_From_String_Several_Digits() {
        assertEquals(new LocalDate(2010, 10, 20),  Interval.fromString("20.10.2010").getDate());
    }

    @Test
    public void Year_Interval_From_String() {
        assertEquals(2010,  Interval.fromString("2010").getYear());
    }

    private Interval interval;

    @Before
    public void setUp() {
        interval = new Interval(new DateTime(), new DateTime());
    }

    @Test
    public void Hash_Code_When_Id_Null() {
        assertEquals(31, new Interval().hashCode());
    }

    @Test
    public void Hash_Code() {
        interval.setId("a");
        assertEquals(31 + "a".hashCode(), interval.hashCode());
    }

    @Test
    public void Equals_Same_Object() {
        assertTrue(interval.equals(interval));
    }

    @Test
    public void Equals_Other_Is_Null() {
        assertFalse(interval.equals(null));
    }

    @Test
    public void Equals_Other_Is_Different_Type() {
        assertFalse(interval.equals("foo"));
    }

    @Test
    public void Equals_Ids_Are_Not_Equal() {
        interval.setId("a");
        Interval other = new Interval();
        other.setId("b");
        assertFalse(interval.equals(other));
    }

    @Test
    public void Equals_Id_Is_Null_And_Other_Id_Not() {
        Interval other = new Interval();
        other.setId("b");
        assertFalse(interval.equals(other));
    }

    @Test
    public void Equals_Ids_Are_Equal() {
        interval.setId("a");
        Interval other = new Interval();
        other.setId("a");
        assertTrue(interval.equals(other));
    }
}
