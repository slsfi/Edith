package fi.finlit.edith.ui.services.hibernate;

import static fi.finlit.edith.sql.domain.QPlace.place;

import java.util.Collection;

import fi.finlit.edith.sql.domain.Place;
import fi.finlit.edith.ui.services.PlaceDao;

public class PlaceDaoImpl extends AbstractDao<Place> implements PlaceDao {
    @Override
    public Collection<Place> findByStartOfName(String partial, int limit) {
        return query()
            .from(place)
            // FIXME!!!!
//            .where(place.normalizedForm().last.startsWithIgnoreCase(partial)).limit(limit)
            .list(place);
    }

    @Override
    public void remove(Long placeId) {
        // FIXME: Hibernatify!
        Place entity = getById(placeId);
        if (entity != null){
            getSession().delete(entity);
        }
    }

    @Override
    public void save(Place place) {
        getSession().save(place);
    }

    @Override
    public void remove(Place place) {
        getSession().delete(place);
    }

    @Override
    public Collection<Place> getAll() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public Place getById(Long id) {
        throw new UnsupportedOperationException("not yet implemented");

    }

}