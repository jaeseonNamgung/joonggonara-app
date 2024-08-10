package com.hit.joonggonara.entity;

import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.dto.request.login.MemberUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@SQLDelete(sql = "UPDATE member SET is_deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member extends BaseEntity{

    @Id @GeneratedValue
    private Long id;


    private String userId;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String name;
    private String nickName;
    private String password;
    private String phoneNumber;
    private String profile;
    @Column(nullable = false)
    private boolean isNotification;

    // 회원 탈퇴 유무 true 일 경우 회원 탈퇴
    private boolean isDeleted;
    // 회원 탈퇴한 시작 시간
    private LocalDateTime deletedAt;



    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;


    @Builder
    public Member (
            String userId,
            String email,
            String name,
            String nickName,
            String password,
            String phoneNumber,
            String profile,
            boolean isNotification,
            LoginType loginType,
            Role role) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.nickName = nickName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.profile = profile;
        this.isNotification = isNotification;
        this.loginType = loginType;
        this.role = role;
    }

    public void update(MemberUpdateRequest memberUpdateRequest) {
        this.nickName = memberUpdateRequest.nickName();
        this.email = memberUpdateRequest.email();
        this.profile = memberUpdateRequest.profile();
        this.phoneNumber = memberUpdateRequest.phoneNumber();
        this.isNotification = memberUpdateRequest.isNotification();
    }

    public void updatePassword(String password){
        this.password = password;
    }
}
