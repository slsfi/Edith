package fi.finlit.edith.util

import java.util.List
import javax.annotation.Nullable
import org.apache.tapestry5.grid.GridDataSource
import org.apache.tapestry5.grid.SortConstraint
import org.apache.tapestry5.hibernate.HibernateSessionManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.mysema.commons.lang.Assert
import com.mysema.query.jpa.JPQLQuery
import com.mysema.query.jpa.hibernate.HibernateQuery
import com.mysema.query.types.EntityPath
import com.mysema.query.types.OrderSpecifier
import com.mysema.query.types.Predicate
import com.mysema.query.types.expr.ComparableExpression
import com.mysema.query.types.path.PathBuilder
import JPQLGridDataSource._
//remove if not needed
import scala.collection.JavaConversions._

object JPQLGridDataSource {

  private val logger = LoggerFactory.getLogger(classOf[JPQLGridDataSource])
}

/**
 * BeanGridDataSource provides an implementation of the GridDataSource for Querydsl hibernate
 *
 * @author tiwe
 */
class JPQLGridDataSource[T]/**
 * Create a new instance with filter conditions
 *
 * @param sessionFactory
 * @param entity
 *            root entity of the query
 * @param defaultOrder
 *            default order for queries, if no order is specified
 * @param caseSensitive
 *            case sensitive ordering
 * @param conditions
 *            filter conditions
 */
(sessionFactory: HibernateSessionManager, 
    entity: EntityPath[T], 
    defaultOrder: OrderSpecifier[_], 
    caseSensitive: Boolean, 
    @Nullable @Nullable private conditions: Predicate) extends GridDataSource {

  private val sessionFactory = Assert.notNull(sessionFactory, "sessionFactory")

  private val entityType = Assert.notNull(entity.getType, "entity has no type").asInstanceOf[Class[T]]

  private val entityPath = new PathBuilder[T](entity.getType, entity.getMetadata)

  private var startIndex: Int = _

  private var preparedResults: List[T] = _

  private val defaultOrder = Assert.notNull(defaultOrder, "defaultOrder")

  /**
   * Create a new instance with no filter conditions
   *
   * @param sessionFactory
   * @param entity
   *            root entity of the query
   * @param defaultOrder
   *            default order for queries, if no order is specified
   * @param caseSensitive
   *            case sensitive ordering
   */
  def this(sessionFactory: HibernateSessionManager, 
      entity: EntityPath[T], 
      defaultOrder: OrderSpecifier[_], 
      caseSensitive: Boolean) {
    this(sessionFactory, entity, defaultOrder, caseSensitive, null)
  }

  private def query(): JPQLQuery = {
    new HibernateQuery(sessionFactory.getSession)
  }

  override def getAvailableRows(): Int = {
    var q = query.from(entityPath)
    if (conditions != null) {
      q.where(conditions)
    }
    q.count().toInt
  }

  override def prepare(start: Int, end: Int, sortConstraints: List[SortConstraint]) {
    var q = query.from(entityPath)
    q.offset(start)
    q.limit(end - start + 1)
    if (sortConstraints.isEmpty) {
      q.orderBy(defaultOrder)
    }
    for (constraint <- sortConstraints) {
      var propertyName = constraint.getPropertyModel.getPropertyName
      @SuppressWarnings("unchecked") var propertyType = constraint.getPropertyModel.getPropertyType
      var propertyPath: ComparableExpression[_] = _
      if (!caseSensitive && propertyType == classOf[String]) {
        propertyPath = entityPath.getString(propertyName).toLowerCase()
      } else {
        propertyPath = entityPath.getComparable(propertyName, propertyType)
      }
      constraint.getColumnSort match {
        case ASCENDING => q.orderBy(propertyPath.asc())
        case DESCENDING => q.orderBy(propertyPath.desc())
      }
    }
    if (conditions != null) {
      q.where(conditions)
    }
    this.startIndex = start
    preparedResults = q.list(entityPath)
  }

  override def getRowValue(index: Int): AnyRef = {
    index = index - startIndex
    if (index < preparedResults.size) {
      return preparedResults.get(index)
    }
    logger.error("Invalid index " + index + " (size " + preparedResults.size + ")")
    null
  }

  override def getRowType(): Class[_] = entityType
}
