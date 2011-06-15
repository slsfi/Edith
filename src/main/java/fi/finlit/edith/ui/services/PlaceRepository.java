package fi.finlit.edith.ui.services;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

import fi.finlit.edith.domain.Place;

@Transactional
public interface PlaceRepository extends Dao<Place, String> {

    Collection<Place> findByStartOfName(String partial, int limit);

    void remove(String placeId);

    void save(Place place);

    void remove(Place place);

}
