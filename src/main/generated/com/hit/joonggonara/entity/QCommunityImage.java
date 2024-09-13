package com.hit.joonggonara.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommunityImage is a Querydsl query type for CommunityImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommunityImage extends EntityPathBase<CommunityImage> {

    private static final long serialVersionUID = 1420459988L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommunityImage communityImage = new QCommunityImage("communityImage");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final QCommunity community;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath fileName = createString("fileName");

    public final StringPath filePath = createString("filePath");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QCommunityImage(String variable) {
        this(CommunityImage.class, forVariable(variable), INITS);
    }

    public QCommunityImage(Path<? extends CommunityImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommunityImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommunityImage(PathMetadata metadata, PathInits inits) {
        this(CommunityImage.class, metadata, inits);
    }

    public QCommunityImage(Class<? extends CommunityImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.community = inits.isInitialized("community") ? new QCommunity(forProperty("community"), inits.get("community")) : null;
    }

}

