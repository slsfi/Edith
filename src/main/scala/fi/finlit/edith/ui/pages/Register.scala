package fi.finlit.edith.ui.pages

import org.apache.tapestry5.annotations.InjectPage
import org.apache.tapestry5.ioc.annotations.Inject
import fi.finlit.edith.sql.domain.Profile
import fi.finlit.edith.sql.domain.User
import fi.finlit.edith.ui.services.UserDao
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

class Register extends Base {

  @BeanProperty
  var user: User = _

  @Inject
  private var userDao: UserDao = _

  @InjectPage
  private var loginPage: Login = _

  def onSuccess(): AnyRef = {
    user.setProfile(Profile.User)
    userDao.save(user)
    loginPage
  }
}
