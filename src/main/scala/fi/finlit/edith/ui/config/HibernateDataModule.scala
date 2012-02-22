package fi.finlit.edith.ui.config

import java.io.IOException
import org.apache.tapestry5.ioc.annotations.Startup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import fi.finlit.edith.ui.services.UserDao
//remove if not needed
import scala.collection.JavaConversions._

object HibernateDataModule {

  private val logger = LoggerFactory.getLogger(classOf[HibernateDataModule])

  @Startup
  def initData(userDao: UserDao) {
    logger.info("Creating users")
    userDao.addUsersFromCsvFile("/users.csv", "ISO-8859-1")
  }
}
