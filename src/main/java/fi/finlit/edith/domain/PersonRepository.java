package fi.finlit.edith.domain;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

import com.mysema.rdfbean.dao.Repository;

@Transactional
public interface PersonRepository extends Repository<Person, String> {

    Collection<Person> findByStartOfFirstAndLastName(String partial, int limit);
}
