package fi.finlit.edith.ui.services.hibernate

import org.apache.tapestry5.ioc.annotations.Inject
import org.springframework.security.userdetails.UserDetails
import org.springframework.security.userdetails.UserDetailsService
import org.springframework.security.userdetails.UsernameNotFoundException
import fi.finlit.edith.dto.UserDetailsImpl
import fi.finlit.edith.sql.domain.User
import fi.finlit.edith.ui.services.UserDao
//remove if not needed
import scala.collection.JavaConversions._

class UserDetailsServiceImpl(userRepository: UserDao) extends UserDetailsService {

  @Inject
  private val userDao = userRepository

  override def loadUserByUsername(username: String): UserDetails = {
    var user = userDao.getByUsername(username)
    if (user != null) {
      return new UserDetailsImpl(user.getUsername, user.getPassword, user.getProfile.getAuthorities)
    }
    throw new UsernameNotFoundException("User " + username + " not found")
  }
}
