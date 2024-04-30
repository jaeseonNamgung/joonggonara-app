package com.hit.joonggonara.entity;

import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@SQLDelete(sql = "UPDATE member SET is_deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Getter
@RequiredArgsConstructor
@Entity
public class Member extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;
    private String email;
    @Column(nullable = false)
    private String name;
    private String nickName;
    private String password;
    private String phoneNumber;

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
            LoginType loginType,
            Role role) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.nickName = nickName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.loginType = loginType;
        this.role = role;
        this.isDeleted = false;
    }
}
