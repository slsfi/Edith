package fi.finlit.edith.ui.services;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Nullable;

/**
 * @author tiwe
 *
 * @param <Entity>
 * @param <Id>
 */
public interface Repository<Entity, Id extends Serializable> {

    /**
     * Get all persisted instances
     *
     * @return
     */
    Collection<Entity> getAll();

    /**
     * Get the persisted instance with the given id
     *
     * @param id
     * @return
     */
    @Nullable
    Entity getById( Id id );

}