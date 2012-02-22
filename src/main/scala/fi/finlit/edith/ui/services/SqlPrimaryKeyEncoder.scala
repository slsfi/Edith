package fi.finlit.edith.ui.services

import java.util.HashMap
import java.util.List
import java.util.Map
import org.apache.tapestry5.ValueEncoder
import org.apache.tapestry5.ioc.internal.util.CollectionFactory
import fi.finlit.edith.Identifiable
//remove if not needed
import scala.collection.JavaConversions._

class SqlPrimaryKeyEncoder[T <: Identifiable](repository: Dao[T, Long]) extends ValueEncoder[T] {

  private val keyToValue = new HashMap[String, T]()

  def getAllValues(): List[T] = {
    var result = CollectionFactory.newList()
    for (entry <- keyToValue.entrySet()) {
      result.add(entry.getValue)
    }
    result
  }

  override def toClient(value: T): String = value.getId.toString

  override def toValue(id: String): T = {
    var rv = repository.getById(Long.valueOf(id))
    keyToValue.put(id, rv)
    rv
  }
}
