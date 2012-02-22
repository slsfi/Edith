package fi.finlit.edith.sql.domain

import org.springframework.security.GrantedAuthority
import org.springframework.security.GrantedAuthorityImpl
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

object Profile {

  class Profile private (roleNames: String*) {

    @BeanProperty
    val authorities = new Array[GrantedAuthority](roleNames.length)

    for (i <- 0 until authorities.length) {
      authorities(i) = new GrantedAuthorityImpl(roleNames(i))
    }
  }

  val Admin = new Profile("ROLE_USER", "ROLE_ADMIN")

  val User = new Profile("ROLE_USER")
}
