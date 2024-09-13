package com.hit.joonggonara.entity;

import com.hit.joonggonara.dto.request.community.CommunityRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Community extends BaseEntity {

    @GeneratedValue
    @Id
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_Id")
    private Member member;

    @OneToMany(mappedBy = "community", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CommunityImage> communityImages = new ArrayList<>();

    @OneToMany(mappedBy = "community", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "community", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Likes> likes = new ArrayList<>();

    @Builder
    public Community(String content, Member member) {
        this.content = content;
        this.member = member;
    }

    public void update(CommunityRequest communityRequest) {
        this.content = communityRequest.content();
    }

}
