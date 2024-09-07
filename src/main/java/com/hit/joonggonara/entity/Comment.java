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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="community_id")
    private Community community;

    @Builder
    public Comment(String content, Community community) {
        this.content = content;
        addCommunity(community);
    }
    private void addCommunity(Community community){
        if(this.community != null){
            this.community.getComments().remove(this);
        }
        this.community = community;
        this.community.getComments().add(this);
    }
}
