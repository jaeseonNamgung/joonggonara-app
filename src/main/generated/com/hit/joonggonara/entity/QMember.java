package com.hit.joonggonara.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -2074084996L;

    public static final QMember member = new QMember("member1");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

<<<<<<< Updated upstream
    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final BooleanPath isNotification = createBoolean("isNotification");

=======
<<<<<<< Updated upstream
=======
    public final BooleanPath isDeleted = createBoolean("isDeleted");

<<<<<<< Updated upstream
    public final BooleanPath isNotification = createBoolean("isNotification");

=======
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
    public final EnumPath<com.hit.joonggonara.common.type.LoginType> loginType = createEnum("loginType", com.hit.joonggonara.common.type.LoginType.class);

    public final StringPath name = createString("name");

    public final StringPath nickName = createString("nickName");

    public final StringPath password = createString("password");

    public final StringPath phoneNumber = createString("phoneNumber");

<<<<<<< Updated upstream
    public final StringPath profile = createString("profile");
=======
<<<<<<< Updated upstream
    public final EnumPath<com.hit.joonggonara.common.type.Role> role = createEnum("role", com.hit.joonggonara.common.type.Role.class);
>>>>>>> Stashed changes

    public final EnumPath<com.hit.joonggonara.common.type.Role> role = createEnum("role", com.hit.joonggonara.common.type.Role.class);

=======
    public final StringPath profile = createString("profile");

<<<<<<< Updated upstream
    public final EnumPath<com.hit.joonggonara.common.type.Role> role = createEnum("role", com.hit.joonggonara.common.type.Role.class);

=======
>>>>>>> Stashed changes
>>>>>>> Stashed changes
    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final StringPath userId = createString("userId");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

