package fi.finlit.edith.ui.services

//remove if not needed
import scala.collection.JavaConversions._

trait AuthService {

  def isAuthenticated(): Boolean

  def logout(): Unit

  def getUsername(): String
}
