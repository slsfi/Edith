package fi.finlit.edith.ui.components.note

import java.util.HashSet
import java.util.Set
import org.apache.tapestry5.Block
import org.apache.tapestry5.ajax.MultiZoneUpdate
import org.apache.tapestry5.annotations.InjectComponent
import org.apache.tapestry5.annotations.Parameter
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.corelib.components.Zone
import org.apache.tapestry5.ioc.annotations.Inject
import fi.finlit.edith.sql.domain.Interval
import fi.finlit.edith.sql.domain.NameForm
import fi.finlit.edith.sql.domain.Person
import fi.finlit.edith.ui.components.InfoMessage
import fi.finlit.edith.ui.services.PersonDao
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

class PersonForm {

  @InjectComponent
  private var infoMessage: InfoMessage = _

  @Property
  private var loopPerson: NameForm = _

  @Parameter(required = true, allowNull = false)
  @Property
  private var closeDialog: Block = _

  @Inject
  private var personDao: PersonDao = _

  @Property
  private var newFirst: String = _

  @Property
  private var newLast: String = _

  @Property
  private var newDescription: String = _

  @Property
  @Parameter
  private var personId: java.lang.Long = _

  @BeanProperty
  var person: Person = _

  @Parameter
  @Property
  private var personZone: Zone = _

  def beginRender() {
    if (personId == null) {
      person = new Person(new NameForm(), new HashSet[NameForm]())
    } else {
      person = personDao.getById(personId)
    }
  }

  def getPersons(): Set[NameForm] = getPerson.getOtherForms

  def getTimeOfBirth(): String = {
    if (getPerson.getTimeOfBirth == null) null else getPerson.getTimeOfBirth.asString()
  }

  def getTimeOfDeath(): String = {
    if (getPerson.getTimeOfDeath == null) null else getPerson.getTimeOfDeath.asString()
  }

  def setTimeOfBirth(time: String) {
    if (time != null) {
      getPerson.setTimeOfBirth(Interval.fromString(time))
    }
  }

  def setTimeOfDeath(time: String) {
    if (time != null) {
      getPerson.setTimeOfDeath(Interval.fromString(time))
    }
  }

  def onPrepareFromPersonForm() {
    if (person == null) {
      person = new Person(new NameForm(), new HashSet[NameForm]())
    }
  }

  def onPrepareFromPersonForm(id: Long) {
    if (person == null) {
      person = personDao.getById(id)
    }
  }

  def onSuccessFromPersonForm(): AnyRef = {
    if (newFirst != null || newLast != null) {
      getPerson.getOtherForms.add(new NameForm(newFirst, newLast, newDescription))
    }
    getPerson.setOtherForms(copyAndRemoveEmptyNameForms(getPerson.getOtherForms))
    if (person.getNormalized.isValid) {
      personDao.save(getPerson)
      personId = getPerson.getId
      infoMessage.addInfoMsg("create-success")
      var update = new MultiZoneUpdate("dialogZone", closeDialog).add("infoMessageZone", infoMessage.getBlock)
      if (personZone != null) {
        update = update.add("personZone", personZone.getBody)
      }
      update
    } else {
      null
    }
  }

  private def copyAndRemoveEmptyNameForms(nameForms: Set[NameForm]): Set[NameForm] = {
    var result = new HashSet[NameForm]()
    for (nameForm <- nameForms if nameForm.isValid) {
      result.add(nameForm)
    }
    result
  }
}
