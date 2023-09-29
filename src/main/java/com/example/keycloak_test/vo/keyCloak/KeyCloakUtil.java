package com.example.keycloak_test.vo.keyCloak;

import com.example.keycloak_test.vo.UserDto;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KeyCloakUtil {
    private final Keycloak keycloak;

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private RealmResource realmResource;

    private UsersResource usersResource;


    public KeyCloakUtil(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public RealmResource getRealmResource() {
        return keycloak.realm(realm);
    }

    public UsersResource getUsersResource() {
        return getRealmResource().users();
    }

    public UserRepresentation createKeyCloakUser(String email){
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(email);
        user.setEmail(email);
        return user;
    }

    public List<UserRepresentation> keyCloakUserFindByEmail(String email){
        return getRealmResource().users()
                .search(email);
    }

    public ClientRepresentation clientFind(){
        return getRealmResource().clients().findByClientId(clientId).get(0);
    }

    public RoleRepresentation clientRoleFind(String roleName){
        return getRealmResource().clients().get(clientFind().getId()).roles().get(roleName).toRepresentation();
    }

   public UserResource getUserResource(String userId){
        return getUsersResource().get(userId);
   }


    /**
     * 유저 Access 토큰 리턴
     * @param userDto
     * @return
     */
    public AccessTokenResponse setAuth(UserDto userDto) {
        Map<String, Object> clientCredentials = new HashMap<>();
        clientCredentials.put("secret", clientSecret);
        clientCredentials.put("grant_type", "password");

        Configuration configuration =
                new Configuration(authServerUrl, realm, clientId, clientCredentials, null);
        AuthzClient authzClient = AuthzClient.create(configuration);

        AccessTokenResponse response =
                authzClient.obtainAccessToken(userDto.getEmail(), userDto.getPassword());

        return response;
    }

    /**
     * 유저 ROLE 권한 주기
     * @param userId
     * @param userRole
     */
    public void userRoleSetting(String userId ,String userRole ){
        getUserResource(userId)
                .roles()
                .clientLevel(
                        clientFind()
                                .getId())
                .add(Collections.singletonList(
                        clientRoleFind(userRole))); // 유저 롤 셋팅
    }

    public CredentialRepresentation getPasswordCred(String password) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false); // 일시적인
        passwordCred.setType(CredentialRepresentation.PASSWORD); // 증명 타입
        passwordCred.setValue(password);
        return passwordCred;
    }
}
