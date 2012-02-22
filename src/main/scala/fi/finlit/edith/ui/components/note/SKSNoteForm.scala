package fi.finlit.edith.ui.components.note

import java.util.Collection
import java.util.HashSet
import java.util.Set
import org.apache.tapestry5.Block
import org.apache.tapestry5.annotations.InjectComponent
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.beaneditor.Validate
import org.apache.tapestry5.corelib.components.Zone
import org.apache.tapestry5.ioc.annotations.Inject
import fi.finlit.edith.sql.domain.NameForm
import fi.finlit.edith.sql.domain.NoteFormat
import fi.finlit.edith.sql.domain.Person
import fi.finlit.edith.sql.domain.Place
import fi.finlit.edith.sql.domain.Term
import fi.finlit.edith.sql.domain.TermLanguage
import fi.finlit.edith.ui.services.PersonDao
import fi.finlit.edith.ui.services.PlaceDao
import fi.finlit.edith.ui.services.TermDao
//remove if not needed
import scala.collection.JavaConversions._

class SKSNoteForm extends AbstractNoteForm {

  @Inject
  @Property
  private var editPersonForm: Block = _

  @Inject
  @Property
  private var editPlaceForm: Block = _

  @Inject
  @Property
  private var editTermForm: Block = _

  @Property
  private var loopPerson: NameForm = _

  @Property
  private var loopPlace: NameForm = _

  @InjectComponent
  @Property
  private var personZone: Zone = _

  @InjectComponent
  @Property
  private var placeZone: Zone = _

  @InjectComponent
  @Property
  private var termZone: Zone = _

  private var place: Place = _

  @Property
  private var placeId: java.lang.Long = _

  @Property
  private var termId: java.lang.Long = _

  @Inject
  private var placeDao: PlaceDao = _

  private var person: Person = _

  @Property
  private var personId: java.lang.Long = _

  @Inject
  private var personDao: PersonDao = _

  private var term: Term = _

  @Inject
  private var termDao: TermDao = _

  @Inject
  @Property
  private var closeDialog: Block = _

  override def onPrepareFromNoteEditForm(noteId: Long) {
    super.onPrepareFromNoteEditForm(noteId)
    if (!isPerson) {
      setPerson(getNoteOnEdit.getPerson)
    }
    if (!isPlace) {
      setPlace(getNoteOnEdit.getPlace)
    }
    if (!isTerm) {
      setTerm(getNoteOnEdit.getTerm)
    }
  }

  override def onSuccessFromNoteEditForm(): AnyRef = {
    getNoteOnEdit.setPerson(getPerson)
    getNoteOnEdit.setPlace(getPlace)
    getNoteOnEdit.setTerm(getTerm)
    super.onSuccessFromNoteEditForm()
  }

  def getTimeOfBirth(): String = {
    if (isPerson && getPerson.getTimeOfBirth != null) {
      return getPerson.getTimeOfBirth.asString()
    }
    null
  }

  def getTimeOfDeath(): String = {
    if (isPerson && getPerson.getTimeOfDeath != null) {
      return getPerson.getTimeOfDeath.asString()
    }
    null
  }

  def isPerson(): Boolean = {
    if (person == null && personId != null) {
      person = personDao.getById(personId)
    }
    person != null
  }

  def isTerm(): Boolean = {
    if (term == null && termId != null) {
      term = termDao.getById(termId)
    }
    term != null
  }

  def isPlace(): Boolean = {
    if (place == null && placeId != null) {
      place = placeDao.getById(placeId)
    }
    place != null
  }

  private def setPerson(person: Person) {
    this.person = person
    if (isPerson) {
      personId = person.getId
    }
  }

  private def setPlace(place: Place) {
    this.place = place
    if (isPlace) {
      placeId = place.getId
    }
  }

  private def setTerm(term: Term) {
    this.term = term
    if (isTerm) {
      termId = term.getId
    }
  }

  private def getPerson(): Person = {
    if (personId != null) {
      return personDao.getById(personId)
    }
    person
  }

  private def getTerm(): Term = {
    if (termId != null) {
      return termDao.getById(termId)
    }
    term
  }

  def onProvideCompletionsFromTerm(partial: String): Collection[Term] = {
    termDao.findByStartOfBasicForm(partial, 10)
  }

  def onProvideCompletionsFromPerson(partial: String): Collection[Person] = {
    personDao.findByStartOfFirstAndLastName(partial, 10)
  }

  def onProvideCompletionsFromPlace(partial: String): Collection[Place] = placeDao.findByStartOfName(partial, 10)

  def getNormalizedDescription(): String = {
    if (isPerson) {
      return getPerson.getNormalized.getDescription
    }
    null
  }

  def getNormalizedFirst(): String = {
    if (isPerson) {
      return getPerson.getNormalized.getFirst
    }
    null
  }

  def getNormalizedLast(): String = {
    if (isPerson) {
      return getPerson.getNormalized.getLast
    }
    null
  }

  def getNormalizedPlaceDescription(): String = {
    if (isPlace) {
      return getPlace.getNormalized.getDescription
    }
    null
  }

  def getNormalizedPlaceName(): String = {
    if (isPlace) {
      return getPlace.getNormalized.getName
    }
    null
  }

  def getPersonInstances(): Int = {
    if (isPerson) {
      return getDocumentNoteDao.getOfPerson(getPerson.getId).size
    }
    0
  }

  def getPersons(): Set[NameForm] = {
    if (isPerson) {
      return getPerson.getOtherForms
    }
    new HashSet[NameForm]()
  }

  private def getPlace(): Place = {
    if (placeId != null) {
      return placeDao.getById(placeId)
    }
    place
  }

  def getPlaceInstances(): Int = {
    if (isPlace) {
      return getDocumentNoteDao.getOfPlace(getPlace.getId).size
    }
    0
  }

  def getPlaces(): Set[NameForm] = {
    if (isPlace) {
      return getPlace.getOtherForms
    }
    new HashSet[NameForm]()
  }

  def onEditPerson(id: java.lang.Long): AnyRef = {
    personId = id
    editPersonForm
  }

  def onEditPlace(id: java.lang.Long): AnyRef = {
    placeId = id
    editPlaceForm
  }

  def onEditTerm(id: java.lang.Long): AnyRef = {
    termId = id
    editTermForm
  }

  def onPerson(id: Long): AnyRef = {
    if (!isPerson) {
      setPerson(personDao.getById(id))
    }
    personZone.getBody
  }

  def onPlace(id: Long): AnyRef = {
    if (!isPlace) {
      setPlace(placeDao.getById(id))
    }
    placeZone.getBody
  }

  def onTerm(id: Long): AnyRef = {
    if (!isTerm) {
      setTerm(termDao.getById(id))
    }
    termZone.getBody
  }

  @Validate("required")
  def getFormat(): NoteFormat = getNoteOnEdit.getFormat

  def setFormat(noteFormat: NoteFormat) {
    super.getNoteOnEdit.setFormat(noteFormat)
  }

  def getTermBasicForm(): String = {
    if (isTerm) {
      return getTerm.getBasicForm
    }
    null
  }

  def getTermMeaning(): String = {
    if (isTerm) {
      return getTerm.getMeaning
    }
    null
  }

  def getTermLanguage(): TermLanguage = {
    if (isTerm) {
      return getTerm.getLanguage
    }
    null
  }
}
