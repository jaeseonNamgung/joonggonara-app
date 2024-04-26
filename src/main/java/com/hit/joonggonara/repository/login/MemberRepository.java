package com.hit.joonggonara.repository.login;

import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.repository.login.querydsl.MemberQueryDsl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryDsl {

    Optional<Member> findByEmail(String username);


}
