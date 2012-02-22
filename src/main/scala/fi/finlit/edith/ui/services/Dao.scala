package fi.finlit.edith.ui.services

import java.io.Serializable
import javax.annotation.Nullable
//remove if not needed
import scala.collection.JavaConversions._

/**
 * @author tiwe
 *
 * @param <Entity>
 * @param <Id>
 */
trait Dao[Entity, Id <: Serializable] {

  /**
   * Get the persisted instance with the given id
   *
   * @param id
   * @return
   */
  def getById(id: Id): Entity
}
