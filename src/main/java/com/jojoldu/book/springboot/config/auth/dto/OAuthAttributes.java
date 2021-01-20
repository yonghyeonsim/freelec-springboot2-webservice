package com.jojoldu.book.springboot.config.auth.dto;

import com.jojoldu.book.springboot.domain.user.Role;
import com.jojoldu.book.springboot.domain.user.User;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name,
            String email, String picture){
        this.attributes = attributes;
        this.name = name;
        this.nameAttributeKey = nameAttributeKey;
        this. email = email;
        this.picture = picture;
    }

    // of() OAuth2User에서 반환하는 사용자 정보는 Map이기 때문에 값 하나하나를 변환해 줘야 한다
    public static OAuthAttributes of(String registrationId, String usernameAttributeName,
                                     Map<String, Object> attributes) {
        if("naver".equals(registrationId)){
            return ofNaver("id", attributes);
        }
        return ofGoogle(usernameAttributeName, attributes);
    }


    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes){
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // User 엔티티를 생성한다
    // OAuthAttributes에서 엔티티를 생성하는 지점은 처음 가입할때 이다
    // 가입할 때의 기본 권한을 GUEST로 주기 위해 role빌더값에는 Role.GUEST를 사용한다
    // OAuthAttributes 클래스 생성이 끝났으면 같은 패키지에 SessionUser 클래스를 생성한다
    public User toEntity(){
        return User.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .role(Role.GUEST)
                .build();
    }
}
