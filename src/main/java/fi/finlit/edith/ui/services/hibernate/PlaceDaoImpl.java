package fi.finlit.edith.ui.services.hibernate;

import static fi.finlit.edith.sql.domain.QPlace.place;
import static fi.finlit.edith.sql.domain.QTerm.term;

import java.util.Collection;

import com.mysema.query.jpa.hibernate.HibernateDeleteClause;

import fi.finlit.edith.sql.domain.Place;
import fi.finlit.edith.ui.services.PlaceDao;

public class PlaceDaoImpl extends AbstractDao<Place> implements PlaceDao {
    @Override
    public Collection<Place> findByStartOfName(String partial, int limit) {
        return query()
            .from(place)
            //.where(place.normalizedForm().last.startsWithIgnoreCase(partial)).limit(limit)
            .list(place);
    }

    @Override
    public void remove(Long placeId) {
        new HibernateDeleteClause(getSession(), place).where(term.id.eq(placeId)).execute();
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
        return query().from(place).list(place);
    }

    @Override
    public Place getById(Long id) {
        return query().from(place).where(place.id.eq(id)).uniqueResult(place);
    }

}