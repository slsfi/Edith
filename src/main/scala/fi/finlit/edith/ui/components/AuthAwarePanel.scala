package fi.finlit.edith.ui.components

import org.apache.tapestry5.ioc.annotations.Inject
import fi.finlit.edith.ui.services.AuthService
//remove if not needed
import scala.collection.JavaConversions._

abstract class AuthAwarePanel {

  @Inject
  private var authService: AuthService = _

  def isLoggedIn(): Boolean = authService.isAuthenticated

  def getUsername(): String = authService.getUsername
}
