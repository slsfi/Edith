package fi.finlit.edith.ui.services

import org.apache.tapestry5.ValueEncoder
import fi.finlit.edith.dto.UserInfo
//remove if not needed
import scala.collection.JavaConversions._

class UserInfoValueEncoder extends ValueEncoder[UserInfo] {

  override def toClient(value: UserInfo): String = value.getUsername

  override def toValue(clientValue: String): UserInfo = new UserInfo(clientValue)
}
