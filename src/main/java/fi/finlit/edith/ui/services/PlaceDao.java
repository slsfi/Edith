package fi.finlit.edith.ui.services;

import java.util.Collection;

import fi.finlit.edith.sql.domain.Place;

public interface PlaceDao extends Dao<Place, Long> {

    Collection<Place> findByStartOfName(String partial, int limit);

    void remove(Long placeId);

    void save(Place place);

    void remove(Place place);

}
