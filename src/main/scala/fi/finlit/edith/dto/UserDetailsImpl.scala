package fi.finlit.edith.dto

import org.springframework.security.GrantedAuthority
import org.springframework.security.userdetails.UserDetails
import UserDetailsImpl._
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

object UserDetailsImpl {

  private val NON_EXPIRED = true

  private val NON_LOCKED = true

  private val ENABLED = true
}

@SerialVersionUID(-3810708516049551503L)
class UserDetailsImpl(@BeanProperty val username: String, @BeanProperty var password: String, @BeanProperty val authorities: GrantedAuthority*) extends UserDetails {

  override def isAccountNonExpired(): Boolean = NON_EXPIRED

  override def isAccountNonLocked(): Boolean = NON_LOCKED

  override def isCredentialsNonExpired(): Boolean = NON_EXPIRED

  override def isEnabled(): Boolean = ENABLED
}
