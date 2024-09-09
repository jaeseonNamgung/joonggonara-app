package com.hit.joonggonara.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> children;


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
    public void addComment(Comment parent){
        if(this.parent != null){
            this.parent.getChildren().remove(this);
        }
        this.parent = parent;
        this.parent.getChildren().add(this);
    }
    public void deleteComment(){
        this.content = "삭제된 댓글입니다.";
        this.isDeleted = true;
    }

}
