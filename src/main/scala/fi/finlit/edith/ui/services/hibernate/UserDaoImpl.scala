package fi.finlit.edith.ui.services.hibernate

import fi.finlit.edith.sql.domain.QUser.user
import java.io.IOException
import java.util.ArrayList
import java.util.Collection
import java.util.List
import org.apache.commons.io.IOUtils
import org.apache.tapestry5.ioc.annotations.EagerLoad
import org.apache.tapestry5.ioc.annotations.Inject
import org.springframework.security.providers.dao.SaltSource
import org.springframework.security.providers.encoding.PasswordEncoder
import com.mysema.query.types.ConstructorExpression
import fi.finlit.edith.dto.UserDetailsImpl
import fi.finlit.edith.dto.UserInfo
import fi.finlit.edith.sql.domain.Profile
import fi.finlit.edith.sql.domain.User
import fi.finlit.edith.ui.services.AuthService
import fi.finlit.edith.ui.services.UserDao
//remove if not needed
import scala.collection.JavaConversions._

@EagerLoad
class UserDaoImpl(@Inject authService: AuthService, @Inject saltSource: SaltSource, @Inject passwordEncoder: PasswordEncoder) extends AbstractDao[User] with UserDao {

  override def getById(id: java.lang.Long): User = {
    query.from(user).where(user.id eq id).uniqueResult(user)
  }

  override def getAll(): Collection[User] = {
    query.from(user).where(user.active eq true).list(user)
  }

  override def getByUsername(username: String): User = {
    query.from(user).where(user.username eq username).uniqueResult(user)
  }

  override def getCurrentUser(): User = getByUsername(authService.getUsername)

  override def getAllUserInfos(): Collection[UserInfo] = {
    query.from(user).where(user.active eq true).list(ConstructorExpression.create(classOf[UserInfo], user.id, user.username))
  }

  override def addUsersFromCsvFile(filePath: String, encoding: String): List[User] = {
    @SuppressWarnings("unchecked") var lines = IOUtils.readLines(classOf[UserDaoImpl].getResourceAsStream(filePath), encoding)
    var users = new ArrayList[User]()
    for (line <- lines) {
      var values = line.split(";")
      var user = getByUsername(values(2))
      if (user == null) {
        user = new User()
      }
      user.setActive(true)
      user.setFirstName(values(0))
      user.setLastName(values(1))
      user.setUsername(values(2))
      user.setEmail(values(3))
      if (values(3).endsWith("mysema.com")) {
        user.setProfile(Profile.Admin)
      } else {
        user.setProfile(Profile.User)
      }
      var userDetails = new UserDetailsImpl(user.getUsername, user.getPassword, user.getProfile.getAuthorities)
      var password = passwordEncoder.encodePassword(user.getUsername, saltSource.getSalt(userDetails))
      user.setPassword(password)
      getSession.save(user)
      users.add(user)
    }
    users
  }

  override def save(user: User) {
    getSession.save(user)
  }
}
