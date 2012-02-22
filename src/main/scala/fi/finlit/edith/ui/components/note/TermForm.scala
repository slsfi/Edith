package fi.finlit.edith.ui.components.note

import org.apache.commons.lang.StringUtils
import org.apache.tapestry5.Block
import org.apache.tapestry5.ajax.MultiZoneUpdate
import org.apache.tapestry5.annotations.InjectComponent
import org.apache.tapestry5.annotations.Parameter
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.corelib.components.Zone
import org.apache.tapestry5.ioc.annotations.Inject
import fi.finlit.edith.sql.domain.Term
import fi.finlit.edith.ui.components.InfoMessage
import fi.finlit.edith.ui.services.TermDao
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

class TermForm {

  @InjectComponent
  private var infoMessage: InfoMessage = _

  @Parameter
  @Property
  private var closeDialog: Block = _

  @Property
  @Parameter
  private var termId: java.lang.Long = _

  @Parameter
  @Property
  private var termZone: Zone = _

  @BeanProperty
  var term: Term = _

  @Inject
  private var termDao: TermDao = _

  def beginRender() {
    if (termId == null) {
      term = new Term()
    } else {
      term = termDao.getById(termId)
    }
  }

  def onPrepareFromTermForm() {
    if (term == null) {
      term = new Term()
    }
  }

  def onPrepareFromTermForm(id: Long) {
    if (term == null) {
      term = termDao.getById(id)
    }
  }

  def onSuccessFromTermForm(): AnyRef = {
    if (StringUtils.isNotBlank(term.getBasicForm)) {
      termDao.save(getTerm)
      termId = getTerm.getId
      infoMessage.addInfoMsg("create-success")
      var update = new MultiZoneUpdate("dialogZone", closeDialog).add("infoMessageZone", infoMessage.getBlock)
      if (termZone != null) {
        update = update.add("termZone", termZone.getBody)
      }
      return update
    }
    null
  }
}
