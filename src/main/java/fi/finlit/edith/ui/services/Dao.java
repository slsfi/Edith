package fi.finlit.edith.ui.services;

import java.io.Serializable;

import javax.annotation.Nullable;

/**
 * @author tiwe
 *
 * @param <Entity>
 * @param <Id>
 */
public interface Dao<Entity, Id extends Serializable> {
    /**
     * Get the persisted instance with the given id
     *
     * @param id
     * @return
     */
    @Nullable
    Entity getById( Id id );

}