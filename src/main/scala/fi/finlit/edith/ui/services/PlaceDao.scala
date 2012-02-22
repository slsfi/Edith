package fi.finlit.edith.ui.services

import java.util.Collection
import org.apache.tapestry5.hibernate.annotations.CommitAfter
import fi.finlit.edith.sql.domain.Place
//remove if not needed
import scala.collection.JavaConversions._

trait PlaceDao extends Dao[Place, Long] {

  def findByStartOfName(partial: String, limit: Int): Collection[Place]

  @CommitAfter
  def remove(placeId: java.lang.Long): Unit

  @CommitAfter
  def save(place: Place): Unit
}
