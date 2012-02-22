package fi.finlit.edith.ui.services

import java.io.IOException
import java.util.Collection
import java.util.List
import javax.annotation.Nullable
import org.apache.tapestry5.hibernate.annotations.CommitAfter
import fi.finlit.edith.dto.UserInfo
import fi.finlit.edith.sql.domain.User
//remove if not needed
import scala.collection.JavaConversions._

trait UserDao extends Dao[User, Long] {

  /**
   * Get the user with the given username
   *
   * @param shortName
   * @return
   */
  def getByUsername(shortName: String): User

  /**
   * Get the current user
   *
   * @param username
   * @return
   */
  def getCurrentUser(): User

  /**
   * @return
   */
  def getAllUserInfos(): Collection[UserInfo]

  @CommitAfter
  def addUsersFromCsvFile(filePath: String, encoding: String): List[User]

  @CommitAfter
  def save(user: User): Unit

  def getAll(): Collection[User]
}
