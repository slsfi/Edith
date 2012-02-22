package fi.finlit.edith.ui.services.hibernate

import org.apache.tapestry5.grid.GridDataSource
import org.apache.tapestry5.hibernate.HibernateSessionManager
import org.apache.tapestry5.ioc.annotations.Inject
import org.hibernate.Session
import com.mysema.query.jpa.JPQLQuery
import com.mysema.query.jpa.hibernate.HibernateQuery
import com.mysema.query.types.EntityPath
import com.mysema.query.types.OrderSpecifier
import com.mysema.query.types.Predicate
import fi.finlit.edith.ui.services.Dao
import fi.finlit.edith.util.JPQLGridDataSource
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

abstract class AbstractDao[T] extends Dao[T, Long] {

  @Inject
  @BeanProperty
  var sessionManager: HibernateSessionManager = _

  protected def query(): JPQLQuery = new HibernateQuery(getSession)

  protected def getSession(): Session = sessionManager.getSession

  protected def createGridDataSource[K](path: EntityPath[K], 
      order: OrderSpecifier[_], 
      caseSensitive: Boolean, 
      filters: Predicate): GridDataSource = {
    new JPQLGridDataSource[K](getSessionManager, path, order, caseSensitive, filters)
  }
}
