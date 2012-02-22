package fi.finlit.edith.ui.services

import nu.localhost.tapestry5.springsecurity.services.LogoutService
import org.apache.tapestry5.ioc.annotations.Inject
import org.springframework.security.Authentication
import org.springframework.security.context.SecurityContextHolder
import org.springframework.security.userdetails.UserDetails
//remove if not needed
import scala.collection.JavaConversions._

class SpringSecurityAuthService extends AuthService {

  @Inject
  private var logoutService: LogoutService = _

  override def isAuthenticated(): Boolean = {
    var auth = SecurityContextHolder.getContext.getAuthentication
    auth != null && auth.getPrincipal.isInstanceOf[UserDetails]
  }

  override def logout() {
    logoutService.logout()
  }

  override def getUsername(): String = {
    var auth = SecurityContextHolder.getContext.getAuthentication
    if (auth != null) auth.getName else null
  }
}
