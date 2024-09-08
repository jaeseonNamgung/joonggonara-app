package com.hit.joonggonara.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseEntity{

    @GeneratedValue
    @Id
    private Long id;

    @Column(nullable = false)
    private  String content;

    @Column
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="community_id")
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @Builder
    public Comment(String content, Community community, Member member) {
        this.content = content;
        this.member = member;
        addCommunity(community);
    }
    private void addCommunity(Community community){
        if(this.community != null){
            this.community.getComments().remove(this);
        }
        this.community = community;
        this.community.getComments().add(this);
    }
    public void deleteComment(){
        this.content = "삭제된 댓글입니다.";
        this.isDeleted = true;
    }
}
