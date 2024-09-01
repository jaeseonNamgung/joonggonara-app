package com.hit.joonggonara.entity;

import com.hit.joonggonara.common.type.CategoryType;
import com.hit.joonggonara.common.type.SchoolType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
@Getter
@Entity
public class Product extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private Long price;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String tradingPlace;

    @Column(nullable = false)
    private String productStatus;

    @Column(nullable = false)
    private boolean isSoldOut;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SchoolType schoolType;

    @Column(nullable = false)
    private boolean isDeleted = false;


    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @OneToMany(mappedBy = "product")
    private List<ChatRoom> chatRooms = new ArrayList<>();



    @Builder
    public Product(Long price, String title, String content, String tradingPlace, String productStatus, boolean isSoldOut, CategoryType categoryType, SchoolType schoolType, Member member) {
        this.price = price;
        this.title = title;
        this.content = content;
        this.tradingPlace = tradingPlace;
        this.productStatus = productStatus;
        this.isSoldOut = isSoldOut;
        this.categoryType = categoryType;
        this.schoolType = schoolType;
        addMember(member);
    }

    public void addMember(Member member){
        if(this.member != null){
            this.member.getProducts().remove(this);
        }
        this.member = member;
        member.getProducts().add(this);
    }

    public void delete(){
        this.isDeleted = true;
    }

    public void updateIsSoldOut() {
        this.isSoldOut = true;
    }
}
