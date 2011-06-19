package fi.finlit.edith.ui.components.note;

import java.util.HashSet;
import java.util.Set;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.sql.domain.NameForm;
import fi.finlit.edith.sql.domain.Place;
import fi.finlit.edith.ui.services.PlaceDao;

@SuppressWarnings("unused")
public class PlaceForm {

    @Property
    private NameForm loopPlace;

    @Parameter
    @Property
    private Block closeDialog;

    @Inject
    private PlaceDao placeDao;

    @Property
    private String newName;

    @Property
    private String newDescription;

    @Property
    @Parameter
    private Long placeId;

    private Place place;

    @Parameter
    @Property
    private Zone placeZone;

    public void beginRender() {
        if (placeId == null) {
            place = new Place(new NameForm(), new HashSet<NameForm>());
        } else {
            place = placeDao.getById(placeId);
        }
    }

    public Place getPlace() {
        return place;
    }

    public Set<NameForm> getPlaces() {
        return getPlace().getOtherForms();
    }

    void onPrepareFromPlaceForm() {
        if (place == null) {
            place = new Place(new NameForm(), new HashSet<NameForm>());
        }
    }

    void onPrepareFromPlaceForm(long id) {
        if (place == null) {
            place = placeDao.getById(id);
        }
    }

    public Object onSuccessFromPlaceForm() {
        if (newName != null) {
            getPlace().getOtherForms().add(new NameForm(newName, newDescription));
        }
        getPlace().setOtherForms(copyAndRemoveEmptyNameForms(getPlace().getOtherForms()));
        placeDao.save(getPlace());
        placeId = getPlace().getId();
        if (placeZone != null) {
            return new MultiZoneUpdate("dialogZone", closeDialog).add("placeZone", placeZone.getBody());
        }
        return closeDialog;
    }

    private Set<NameForm> copyAndRemoveEmptyNameForms(Set<NameForm> nameForms) {
        Set<NameForm> result = new HashSet<NameForm>();
        for (NameForm nameForm : nameForms) {
            if (nameForm.getFirst() != null || nameForm.getLast() != null) {
                result.add(nameForm);
            }
        }
        return result;
    }
}
