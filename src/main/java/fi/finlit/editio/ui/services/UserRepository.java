package fi.finlit.editio.ui.services;

import java.util.List;

import fi.finlit.editio.domain.User;

/**
 * UserRepository provides
 *
 * @author tiwe
 * @version $Id$
 */
public interface UserRepository {
    
    User getByUsername(String shortName);
    
    List<User> getOrderedByName();

}
