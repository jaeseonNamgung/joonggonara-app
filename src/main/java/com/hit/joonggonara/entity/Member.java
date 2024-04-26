package com.hit.joonggonara.entity;

import com.hit.joonggonara.type.LoginType;
import com.hit.joonggonara.type.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Entity
public class Member extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    @Column(nullable = false)
    private String name;
    private String nickName;
    private String password;
    private String phoneNumber;
    private String school;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;


    @Builder
    public Member (
            String email,
            String name,
            String nickName,
            String password,
            String phoneNumber,
            String school,
            LoginType loginType,
            Role role) {
        this.email = email;
        this.name = name;
        this.nickName = nickName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.school = school;
        this.loginType = loginType;
        this.role = role;
    }
}
