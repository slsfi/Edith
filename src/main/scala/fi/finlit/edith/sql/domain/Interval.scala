package fi.finlit.edith.sql.domain

import javax.persistence.Column
import javax.persistence.Embeddable
import org.hibernate.annotations.Type
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import Interval._
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

object Interval {

  private val formatter = DateTimeFormat.forPattern("d.M.y").withZone(DateTimeZone.UTC)

  def createYear(year: Int): Interval = {
    new Interval(new DateTime(year, 1, 1, 0, 0, 0, 0), new DateTime(year + 1, 1, 1, 0, 0, 0, 0))
  }

  /**
   * This creates an empty interval for the start of the given date
   *
   * @param localDate
   * @return
   */
  def createDate(localDate: LocalDate): Interval = {
    Interval.createDate(localDate.toDateTimeAtStartOfDay())
  }

  /**
   * This creates an empty interval for the given timestamp
   *
   * @param dateTime
   * @return
   */
  def createDate(dateTime: DateTime): Interval = new Interval(dateTime, dateTime)

  def fromString(s: String): Interval = {
    try {
      Interval.createDate(formatter.parseDateTime(s))
    } catch {
      case e: IllegalArgumentException => Interval.createYear(Integer.parseInt(s))
    }
  }
}

@Embeddable
class Interval() {

  @Column(name = "start_date_time")
  @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
  @BeanProperty
  var start: DateTime = _

  @Column(name = "end_date_time")
  @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
  @BeanProperty
  var end: DateTime = _

  def this(start: DateTime, end: DateTime) {
    this.start = start.withZoneRetainFields(DateTimeZone.UTC)
    this.end = end.withZoneRetainFields(DateTimeZone.UTC)
  }

  def isDate(): Boolean = getStart == getEnd

  def isYear(): Boolean = !isDate

  def getYear(): Int = getStart.getYear

  def getDate(): LocalDate = getStart.toLocalDate()

  def asString(): String = {
    if (isYear) {
      return String.valueOf(getYear)
    }
    formatter.print(getDate)
  }
}
