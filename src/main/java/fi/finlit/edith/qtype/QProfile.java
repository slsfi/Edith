package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import fi.finlit.edith.domain.Profile;


/**
 * QProfile is a Querydsl query type for Profile
 */
public class QProfile extends EnumPath<Profile> {

    private static final long serialVersionUID = -1060278778;

    public static final QProfile profile = new QProfile("profile");

    public QProfile(String variable) {
        super(Profile.class, forVariable(variable));
    }

    public QProfile(BeanPath<Profile> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QProfile(PathMetadata<?> metadata) {
        super(Profile.class, metadata);
    }

}

