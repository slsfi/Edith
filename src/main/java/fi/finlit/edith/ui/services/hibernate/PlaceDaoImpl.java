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
            .where(place.normalized.last.startsWithIgnoreCase(partial)).limit(limit)
            .list(place);
    }

    @Override
    public void remove(Long placeId) {
        Place place = getById(placeId);
        getSession().delete(place);
    }

    @Override
    public void save(Place place) {
        getSession().save(place);
    }

    @Override
    public Place getById(Long id) {
        return query().from(place).where(place.id.eq(id)).uniqueResult(place);
    }

}