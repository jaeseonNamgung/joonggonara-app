package com.hit.joonggonara.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Entity
public class Likes extends BaseEntity{

    @GeneratedValue
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    @Builder
    public Likes(Long id, Member member, Community community) {
        this.id = id;
        this.member = member;
        addCommunity(community);
    }

    public void addCommunity(Community community) {
        if(this.community != null) {
            this.community.getLikes().remove(this);
        }
        this.community = community;
        this.community.getLikes().add(this);
    }
}
