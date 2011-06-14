package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import fi.finlit.edith.domain.User;


/**
 * QUser is a Querydsl query type for User
 */
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 1090038926;

    public static final QUser user = new QUser("user");

    public final QIdentifiable _super = new QIdentifiable(this);

    public final StringPath email = createString("email");

    public final StringPath firstName = createString("firstName");

    //inherited
    public final StringPath id = _super.id;

    public final StringPath lastName = createString("lastName");

    public final StringPath password = createString("password");

    // custom
    public final QProfile profile = new QProfile(forProperty("profile"));

    public final StringPath username = createString("username");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(BeanPath<? extends User> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QUser(PathMetadata<?> metadata) {
        super(User.class, metadata);
    }

}

