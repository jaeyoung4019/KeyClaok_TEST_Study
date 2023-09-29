package com.example.keycloak_test.service;


import com.example.keycloak_test.vo.UserDto;
import com.example.keycloak_test.vo.UserUpdateDto;
import com.example.keycloak_test.vo.keyCloak.KeyCloakUtil;
import com.example.keycloak_test.vo.response_util.RestException;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.List;
@Slf4j
@Service
public class AuthService {

    private final Keycloak keycloak;
    private final KeyCloakUtil keyCloakUtil;

    public AuthService(Keycloak keycloak, KeyCloakUtil keyCloakUtil) {
        this.keycloak = keycloak;
        this.keyCloakUtil = keyCloakUtil;
    }

    /**
     * 비밀번호 변경 테스트    [ 성공 ]
     * @param userUpdateDto
     */
    public int updateUserPassword(UserUpdateDto userUpdateDto) throws RestException {
        try {
            List<UserRepresentation> userRepresentations = keyCloakUtil.keyCloakUserFindByEmail(userUpdateDto.getEmail());
            UserResource userResource = keyCloakUtil.getUsersResource().get(userRepresentations.get(0).getId());
            userResource.resetPassword(keyCloakUtil.getPasswordCred(userUpdateDto.getNewPassword()));
        } catch (Exception e) {
            throw new RestException(404, "알맞지 않은 값을 입력하였습니다.");
        }
        return 1;
    }

    public UserDto createUser(UserDto userDto) throws RestException {
        // 유저정보 세팅
        UserRepresentation user = keyCloakUtil.createKeyCloakUser(userDto.getEmail());
        // 유저 생성
        Response response = keyCloakUtil.getUsersResource().create(user);
        if(response.getStatus() == 201) {
            // 유저 생성
            String userId = CreatedResponseUtil.getCreatedId(response);
            // create password credential 자격 증명 표현
            CredentialRepresentation passwordCred = keyCloakUtil.getPasswordCred(userDto.getPassword());
            // 만든 유저 리소스 가져오기
            UserResource userResource = keyCloakUtil.getUserResource(userId);
            // 비밀번호 셋팅하기
            userResource.resetPassword(passwordCred);
            // role 세팅
            keyCloakUtil.userRoleSetting(userId ,userDto.getUserRole().getCode() );
        } else {
            throw new RestException( 403 , "알맞지 않은 값을 입력하였습니다.");
        }
        return userDto;
    }


    /*
     *  사용자 존재하는지 체크
     * */
    public boolean existsByUsername(String userName) {

        List<UserRepresentation> search = keyCloakUtil.getRealmResource().users()
                .search(userName);
        for (UserRepresentation userRepresentation : search) {
            System.out.println("userRepresentation.getId() = " + userRepresentation.getId());
            System.out.println("userRepresentation.getEmail() = " + userRepresentation.getEmail());
        }
        if(search.size() > 0){
            log.debug("search : {}", search.get(0).getUsername());
            if(search.get(0).getUsername().equals(userName)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 스프링 권한 부여하기
     * @param userDto
     * @return
     */
   public AccessTokenResponse setAuth(UserDto userDto) {
        return keyCloakUtil.setAuth(userDto);
   }
}