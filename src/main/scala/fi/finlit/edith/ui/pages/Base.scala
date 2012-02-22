package fi.finlit.edith.ui.pages

import org.apache.tapestry5.ioc.annotations.Inject
import fi.finlit.edith.ui.services.AuthService
//remove if not needed
import scala.collection.JavaConversions._

abstract class Base {

  @Inject
  private var authService: AuthService = _

  def isLoggedIn(): Boolean = authService.isAuthenticated

  def getUsername(): String = authService.getUsername
}
