package com.example.member.entity;


import com.example.member.constant.UserRole;
import com.example.member.dto.MemberFormDto;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

// RDBMS(관계형데이터베이스->mysql등) Table을 객체화
// JPA로 관리되는 엔티티 객체 의미
@Entity
// 별도의 이름을 가진 데이터베이스 테이블과 매핑 (엔티티 클래스를 어떤 테이블로 생성할 것이냐)
// Entity의 클래스명과 데이터베이스의 테이블명이 다를 경우 name=""로 매핑
@Getter
@Setter
@ToString
// NoArgs 와 AllArgs가 같이 있어야 에러가 발생하지 않는다.
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseEntity {

    // primary key를 가진 변수 선언
    @Id
    // 해당 id 값을 어떻게 자동으로 생성할 지 전략 선택.
    // 각 데이터베이스에 따라 기본키 자동 생성 (mysql은 identity)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 기본적으로 멤버 변수명과 일치하는 데이터베이스 컬럼을 매핑
    // 선언이 꼭 필요한 것은 아님
    // @Column에서 지정한 변수명과 데이터베이스 컬럼명을 서로 다르게 주고 싶다면 (name = "")
    @Column(name = "member_id")
    private Long Id;

    @Column
    private String name;

    @Column
    private String nickName;

    // 특정 열에 중복값이 입력되지 않는다?
    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String phoneN1;
    @Column
    private String phoneN2;
    @Column
    private String phoneN3;


    @Column
    private String postcode;   // 우편 번호
    @Column
    private String address;     // 주소
    @Column
    private String detailAddress; // 상세주소
    @Column
    private String extraAddress; // 참고항목

    // @Enumberated : 자바 enum 타입을 엔티티 클래스의 속성으로 사용할 수 있다.
    // EnumType.STRING : 각 Enum 이름을 저장한다 (USER, ADMIN)
    // EnumType.ORDINAL : 각 Enum에 대응하는 순서를 컬럼에 저장한다. (0, 1, 2..)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private String provider;    // oauth2를 이용할 경우 어떤 플랫폼을 이용하는지
    private String providerId;  // oauth2를 이용할 경우 아이디값

    // 회원가입시 유저롤 선택
    private String joinUserRole;


    @Builder(builderClassName = "OAuth2Register", builderMethodName = "oauth2Register")
    public Member(String name, String password, String email, UserRole role, String provider, String providerId) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.userRole = role;
        this.provider = provider;
        this.providerId = providerId;
    }


    public static Member toMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        Member member = new Member();

        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setPassword(passwordEncoder.encode(memberFormDto.getPassword()));
        member.setPhoneN1(memberFormDto.getPhoneN1());
        member.setPhoneN2(memberFormDto.getPhoneN2());
        member.setPhoneN3(memberFormDto.getPhoneN3());
        member.setPostcode(memberFormDto.getPostcode());
        member.setAddress(memberFormDto.getAddress());
        member.setDetailAddress(memberFormDto.getDetailAddress());
        member.setExtraAddress(memberFormDto.getExtraAddress());
        if (memberFormDto.getJoinUserRole().equals("0")) {
            member.setUserRole(UserRole.USER);
        } else if(memberFormDto.getJoinUserRole().equals("1")){
            member.setUserRole(UserRole.ADMIN);
        }
        return member;

    }


}