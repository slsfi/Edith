package fi.finlit.edith.ui.config

import java.io.IOException
import java.io.InputStream
import java.util.Calendar
import java.util.Map
import java.util.Properties
import org.apache.tapestry5.SymbolConstants
import org.apache.tapestry5.ioc.MappedConfiguration
import org.apache.tapestry5.ioc.ServiceBinder
import org.apache.tapestry5.ioc.annotations.Startup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import fi.finlit.edith.ui.services.AuthService
import fi.finlit.edith.ui.services.SpringSecurityAuthService
import fi.finlit.edith.ui.services.content.ContentRenderer
import fi.finlit.edith.ui.services.content.ContentRendererImpl
import fi.finlit.edith.ui.services.svn.SubversionService
import fi.finlit.edith.ui.services.svn.SubversionServiceImpl
//remove if not needed
import scala.collection.JavaConversions._

object ServiceModule {

  private val logger = LoggerFactory.getLogger(classOf[HibernateDataModule])

  def bind(binder: ServiceBinder) {
    binder.bind(classOf[SubversionService], classOf[SubversionServiceImpl])
    binder.bind(classOf[ContentRenderer], classOf[ContentRendererImpl])
    binder.bind(classOf[AuthService], classOf[SpringSecurityAuthService])
  }

  @Startup
  def initData(subversionService: SubversionService) {
    logger.info("Starting up subversion")
    subversionService.initialize()
  }

  def contributeApplicationDefaults(configuration: MappedConfiguration[String, String]) {
    var properties = new Properties()
    var stream: InputStream = null
    try {
      stream = classOf[ServiceModule].getResourceAsStream("/edith.properties")
      properties.load(stream)
      if (properties.getProperty(SymbolConstants.APPLICATION_VERSION) == null) {
        configuration.add(SymbolConstants.APPLICATION_VERSION, String.valueOf(Calendar.getInstance.getTimeInMillis))
      }
      for (entry <- properties.entrySet()) {
        configuration.add(entry.getKey.toString, entry.getValue.toString)
      }
    } finally {
      if (stream != null) {
        stream.close()
      }
    }
  }
}
