package fi.finlit.edith.ui.services

import org.apache.tapestry5.ValueEncoder
import org.apache.tapestry5.services.ValueEncoderFactory
import fi.finlit.edith.dto.UserInfo
//remove if not needed
import scala.collection.JavaConversions._

class UserInfoValueEncoderFactory extends ValueEncoderFactory[UserInfo] {

  override def create(`type`: Class[UserInfo]): ValueEncoder[UserInfo] = new UserInfoValueEncoder()
}
