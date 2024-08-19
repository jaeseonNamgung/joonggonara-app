package com.hit.joonggonara.entity;

import com.hit.joonggonara.common.type.CategoryType;
import com.hit.joonggonara.common.type.SchoolType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

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
        this.member = member;
    }


}
