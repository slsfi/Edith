package fi.finlit.edith.domain;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

import com.mysema.rdfbean.dao.Repository;

@Transactional
public interface PlaceRepository extends Repository<Place, String> {
    Collection<Place> findByStartOfName(String partial, int limit);
}
