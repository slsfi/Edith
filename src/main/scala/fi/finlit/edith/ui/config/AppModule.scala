package fi.finlit.edith.ui.config

import nu.localhost.tapestry5.springsecurity.services.RequestInvocationDefinition
import org.apache.tapestry5.SymbolConstants
import org.apache.tapestry5.ioc.Configuration
import org.apache.tapestry5.ioc.MappedConfiguration
import org.apache.tapestry5.ioc.OrderedConfiguration
import org.apache.tapestry5.ioc.ServiceBinder
import org.apache.tapestry5.ioc.annotations.InjectService
import org.apache.tapestry5.ioc.annotations.SubModule
import org.apache.tapestry5.services.ValueEncoderFactory
import org.springframework.security.providers.AuthenticationProvider
import org.springframework.security.providers.encoding.PasswordEncoder
import org.springframework.security.providers.encoding.ShaPasswordEncoder
import org.springframework.security.userdetails.UserDetailsService
import fi.finlit.edith.ui.services.UserInfoValueEncoderFactory
import fi.finlit.edith.ui.services.hibernate.UserDetailsServiceImpl
//remove if not needed
import scala.collection.JavaConversions._

object AppModule {

  def contributeApplicationDefaults(configuration: MappedConfiguration[String, String]) {
    configuration.add(SymbolConstants.SUPPORTED_LOCALES, "fi,en,sv,de")
    configuration.add(SymbolConstants.PRODUCTION_MODE, System.getProperty("production.mode", "false"))
    configuration.add(SymbolConstants.FORM_CLIENT_LOGIC_ENABLED, "false")
    configuration.add("spring-security.loginform.url", "/login")
    configuration.add("spring-security.check.url", "/security_check")
    configuration.add("spring-security.failure.url", "/loginfailed")
  }

  def contributeFilterSecurityInterceptor(configuration: Configuration[RequestInvocationDefinition]) {
    configuration.add(new RequestInvocationDefinition("/loginfailed", "ROLE_ANONYMOUS"))
    configuration.add(new RequestInvocationDefinition("/security_check", "ROLE_ANONYMOUS"))
    configuration.add(new RequestInvocationDefinition("/login", "ROLE_ANONYMOUS"))
    configuration.add(new RequestInvocationDefinition("/favicon.ico", "ROLE_ANONYMOUS,ROLE_USER"))
    configuration.add(new RequestInvocationDefinition("/about", "ROLE_ANONYMOUS,ROLE_USER"))
    configuration.add(new RequestInvocationDefinition("/assets/**", "ROLE_ANONYMOUS,ROLE_USER"))
    configuration.add(new RequestInvocationDefinition("/**", "ROLE_USER"))
  }

  def bind(binder: ServiceBinder) {
    binder.bind(classOf[UserDetailsService], classOf[UserDetailsServiceImpl])
  }

  def contributeServiceOverride(configuration: MappedConfiguration[Class[_], Any]) {
    configuration.add(classOf[PasswordEncoder], new ShaPasswordEncoder())
  }

  def contributeProviderManager(configuration: OrderedConfiguration[AuthenticationProvider], @InjectService("DaoAuthenticationProvider") daoAuthenticationProvider: AuthenticationProvider) {
    configuration.add("daoAuthenticationProvider", daoAuthenticationProvider)
  }

  def contributeClasspathAssetAliasManager(configuration: MappedConfiguration[String, String]) {
    configuration.add("js", "js")
  }

  def contributeValueEncoderSource(configuration: MappedConfiguration[Class[_], ValueEncoderFactory[_]]) {
    configuration.add(classOf[fi.finlit.edith.dto.UserInfo], new UserInfoValueEncoderFactory())
  }
}
