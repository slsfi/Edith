package fi.finlit.edith.qtype;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;
import com.mysema.query.types.expr.*;

import fi.finlit.edith.domain.UserInfo;


/**
 * QUserInfo is a Querydsl query type for UserInfo
 */
public class QUserInfo extends EntityPathBase<UserInfo> {

    private static final long serialVersionUID = -2068410788;

    public static ConstructorExpression<UserInfo> create(StringExpression username) {
        return new ConstructorExpression<UserInfo>(UserInfo.class, new Class[]{String.class}, username);
    }

    public static final QUserInfo userInfo = new QUserInfo("userInfo");

    public final QIdentifiable _super = new QIdentifiable(this);

    //inherited
    public final StringPath id = _super.id;

    public final StringPath username = createString("username");

    public QUserInfo(String variable) {
        super(UserInfo.class, forVariable(variable));
    }

    public QUserInfo(BeanPath<? extends UserInfo> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QUserInfo(PathMetadata<?> metadata) {
        super(UserInfo.class, metadata);
    }

}

