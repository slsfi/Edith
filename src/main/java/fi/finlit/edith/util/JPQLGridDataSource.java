package fi.finlit.edith.util;

/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
import java.util.List;

import javax.annotation.Nullable;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysema.commons.lang.Assert;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.ComparableExpression;
import com.mysema.query.types.path.PathBuilder;

/**
 * BeanGridDataSource provides an implementation of the GridDataSource for Querydsl hibernate
 * 
 * @author tiwe
 */
public class JPQLGridDataSource<T> implements GridDataSource {

    private static final Logger logger = LoggerFactory.getLogger(JPQLGridDataSource.class);

    private final HibernateSessionManager sessionFactory;

    private final Class<T> entityType;

    private final PathBuilder<T> entityPath;

    private int startIndex;

    private List<T> preparedResults;

    @Nullable
    private final Predicate conditions;

    private final OrderSpecifier<?> defaultOrder;

    private final boolean caseSensitive;

    // TODO Add switch to have uniqueresults

    /**
     * Create a new instance with no filter conditions
     * 
     * @param sessionFactory
     * @param entity
     *            root entity of the query
     * @param defaultOrder
     *            default order for queries, if no order is specified
     * @param caseSensitive
     *            case sensitive ordering
     */
    public JPQLGridDataSource(HibernateSessionManager sessionFactory, EntityPath<T> entity,
            OrderSpecifier<?> defaultOrder, boolean caseSensitive) {
        this(sessionFactory, entity, defaultOrder, caseSensitive, null);
    }

    /**
     * Create a new instance with filter conditions
     * 
     * @param sessionFactory
     * @param entity
     *            root entity of the query
     * @param defaultOrder
     *            default order for queries, if no order is specified
     * @param caseSensitive
     *            case sensitive ordering
     * @param conditions
     *            filter conditions
     */
    @SuppressWarnings("unchecked")
    public JPQLGridDataSource(HibernateSessionManager sessionFactory, EntityPath<T> entity,
            OrderSpecifier<?> defaultOrder, boolean caseSensitive, @Nullable Predicate conditions) {
        this.sessionFactory = Assert.notNull(sessionFactory, "sessionFactory");
        this.entityType = (Class<T>) Assert.notNull(entity.getType(), "entity has no type");
        this.entityPath = new PathBuilder<T>(entity.getType(), entity.getMetadata());
        this.defaultOrder = Assert.notNull(defaultOrder, "defaultOrder");
        this.conditions = conditions;
        this.caseSensitive = caseSensitive;
    }

    private JPQLQuery query() {
        return new HibernateQuery(sessionFactory.getSession());
    }

    @Override
    public int getAvailableRows() {
        JPQLQuery q = query().from(entityPath);
        if (conditions != null) {
            q.where(conditions);
        }
        return (int) q.count();
    }

    @Override
    public void prepare(final int start, final int end, final List<SortConstraint> sortConstraints) {
        JPQLQuery q = query().from(entityPath);
        q.offset(start);
        q.limit(end - start + 1);
        if (sortConstraints.isEmpty()) {
            q.orderBy(defaultOrder);
        }
        for (SortConstraint constraint : sortConstraints) {
            String propertyName = constraint.getPropertyModel().getPropertyName();
            @SuppressWarnings("unchecked")
            Class<? extends Comparable<?>> propertyType = constraint.getPropertyModel()
                    .getPropertyType();
            ComparableExpression<?> propertyPath;
            if (!caseSensitive && propertyType.equals(String.class)) {
                propertyPath = entityPath.getString(propertyName).toLowerCase();
            } else {
                propertyPath = entityPath.getComparable(propertyName, propertyType);
            }

            switch (constraint.getColumnSort()) {
            case ASCENDING:
                q.orderBy(propertyPath.asc());
                break;
            case DESCENDING:
                q.orderBy(propertyPath.desc());
                break;
            }
        }
        if (conditions != null) {
            q.where(conditions);
        }
        this.startIndex = start;
        preparedResults = q.list(entityPath);
    }

    @Override
    public Object getRowValue(int index) {
        index = index - startIndex;
        if (index < preparedResults.size()) {
            return preparedResults.get(index);
        } else {
            logger.error("Invalid index " + index + " (size " + preparedResults.size() + ")");
            return null;
        }
    }

    @Override
    public Class<?> getRowType() {
        return entityType;
    }

}
