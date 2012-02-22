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
import fi.finlit.edith.sql.domain.NameForm
import fi.finlit.edith.sql.domain.Place
import fi.finlit.edith.ui.components.InfoMessage
import fi.finlit.edith.ui.services.PlaceDao
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

class PlaceForm {

  @Property
  private var loopPlace: NameForm = _

  @InjectComponent
  private var infoMessage: InfoMessage = _

  @Parameter
  @Property
  private var closeDialog: Block = _

  @Inject
  private var placeDao: PlaceDao = _

  @Property
  private var newName: String = _

  @Property
  private var newDescription: String = _

  @Property
  @Parameter
  private var placeId: java.lang.Long = _

  @BeanProperty
  var place: Place = _

  @Parameter
  @Property
  private var placeZone: Zone = _

  def beginRender() {
    if (placeId == null) {
      place = new Place(new NameForm(), new HashSet[NameForm]())
    } else {
      place = placeDao.getById(placeId)
    }
  }

  def getPlaces(): Set[NameForm] = getPlace.getOtherForms

  def onPrepareFromPlaceForm() {
    if (place == null) {
      place = new Place(new NameForm(), new HashSet[NameForm]())
    }
  }

  def onPrepareFromPlaceForm(id: Long) {
    if (place == null) {
      place = placeDao.getById(id)
    }
  }

  def onSuccessFromPlaceForm(): AnyRef = {
    if (newName != null) {
      getPlace.getOtherForms.add(new NameForm(newName, newDescription))
    }
    getPlace.setOtherForms(copyAndRemoveEmptyNameForms(getPlace.getOtherForms))
    if (getPlace.getNormalized.isValid) {
      placeDao.save(getPlace)
      placeId = getPlace.getId
      infoMessage.addInfoMsg("create-success")
      var update = new MultiZoneUpdate("dialogZone", closeDialog).add("infoMessageZone", infoMessage.getBlock)
      if (placeZone != null) {
        update = update.add("placeZone", placeZone.getBody)
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
