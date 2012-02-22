package fi.finlit.edith.ui.config

import org.apache.tapestry5.hibernate.HibernateTransactionAdvisor
import org.apache.tapestry5.ioc.Configuration
import org.apache.tapestry5.ioc.MethodAdviceReceiver
import org.apache.tapestry5.ioc.ServiceBinder
import org.apache.tapestry5.ioc.annotations.Match
import fi.finlit.edith.ui.services.DocumentDao
import fi.finlit.edith.ui.services.DocumentNoteDao
import fi.finlit.edith.ui.services.NoteDao
import fi.finlit.edith.ui.services.PersonDao
import fi.finlit.edith.ui.services.PlaceDao
import fi.finlit.edith.ui.services.TermDao
import fi.finlit.edith.ui.services.UserDao
import fi.finlit.edith.ui.services.hibernate.DocumentDaoImpl
import fi.finlit.edith.ui.services.hibernate.DocumentNoteDaoImpl
import fi.finlit.edith.ui.services.hibernate.NoteDaoImpl
import fi.finlit.edith.ui.services.hibernate.PersonDaoImpl
import fi.finlit.edith.ui.services.hibernate.PlaceDaoImpl
import fi.finlit.edith.ui.services.hibernate.TermDaoImpl
import fi.finlit.edith.ui.services.hibernate.UserDaoImpl
//remove if not needed
import scala.collection.JavaConversions._

object HibernateServiceModule {

  @Match("*Dao")
  def adviseTransactions(advisor: HibernateTransactionAdvisor, receiver: MethodAdviceReceiver) {
    advisor.addTransactionCommitAdvice(receiver)
  }

  def bind(binder: ServiceBinder) {
    binder.bind(classOf[UserDao], classOf[UserDaoImpl])
    binder.bind(classOf[NoteDao], classOf[NoteDaoImpl])
    binder.bind(classOf[DocumentDao], classOf[DocumentDaoImpl])
    binder.bind(classOf[DocumentNoteDao], classOf[DocumentNoteDaoImpl])
    binder.bind(classOf[PersonDao], classOf[PersonDaoImpl])
    binder.bind(classOf[PlaceDao], classOf[PlaceDaoImpl])
    binder.bind(classOf[TermDao], classOf[TermDaoImpl])
  }

  def contributeHibernateEntityPackageManager(configuration: Configuration[String]) {
    configuration.add("fi.finlit.edith.sql.domain")
  }
}
