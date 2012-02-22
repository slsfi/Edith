package fi.finlit.edith.ui.services.hibernate

import fi.finlit.edith.sql.domain.QPlace.place
import java.util.Collection
import fi.finlit.edith.sql.domain.Place
import fi.finlit.edith.ui.services.PlaceDao
//remove if not needed
import scala.collection.JavaConversions._

class PlaceDaoImpl extends AbstractDao[Place] with PlaceDao {

  override def findByStartOfName(partial: String, limit: Int): Collection[Place] = {
    query.from(place).where(place.normalized.last.startsWithIgnoreCase(partial)).limit(limit).list(place)
  }

  override def remove(placeId: java.lang.Long) {
    var place = getById(placeId)
    getSession.delete(place)
  }

  override def save(place: Place) {
    getSession.save(place)
  }

  override def getById(id: java.lang.Long): Place = {
    query.from(place).where(place.id eq id).uniqueResult(place)
  }
}
