package com.hit.joonggonara.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CommunityImage extends BaseEntity{

    @GeneratedValue
    @Id
    private Long id;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="community_id")
    private Community community;

    @Builder
    public CommunityImage(String filePath, String fileName, Community community) {
        this.filePath = filePath;
        this.fileName = fileName;
        addCommunity(community);
    }

    private void addCommunity(Community community){
        if(this.community != null){
            this.community.getCommunityImages().remove(this);
        }
        this.community = community;
        this.community.getCommunityImages().add(this);
    }
}
