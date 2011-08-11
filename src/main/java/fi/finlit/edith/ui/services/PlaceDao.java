package fi.finlit.edith.ui.services;

import java.util.Collection;

import org.apache.tapestry5.hibernate.annotations.CommitAfter;

import fi.finlit.edith.sql.domain.Place;

public interface PlaceDao extends Dao<Place, Long> {

    Collection<Place> findByStartOfName(String partial, int limit);

    @CommitAfter
    void remove(Long placeId);

    @CommitAfter
    void save(Place place);

}
