package com.example.keycloak_test.controller;

import com.example.keycloak_test.service.AuthService;
import com.example.keycloak_test.vo.UserDto;
import com.example.keycloak_test.vo.UserUpdateDto;
import com.example.keycloak_test.vo.response_util.Response;
import com.example.keycloak_test.vo.response_util.RestException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class AuthController {

    private final AuthService authService;

    /*
     * 회원가입
     * */
    @PostMapping("/signup")
    @ApiOperation(value = "회원가입")
    public Response<UserDto> registerUser(@RequestBody @ApiParam(value = "회원가입 유저 정보") UserDto userDto) throws RestException {
        if(authService.existsByUsername(userDto.getEmail())) {
          throw new RestException( 403 , "해당 유저는 존재합니다.");
        }
        return new Response.ResponseBuilder<UserDto>( "회원가입 성공" , 200)
                .resultResponse(authService.createUser(userDto))
                .total(1)
                .build();
    }

    @PutMapping("/update")
    @ApiOperation(value = "유저 정보 업데이트")
    public Response<Integer> updateUser(@RequestBody @ApiParam(value = "회원가입 유저 정보") UserUpdateDto userDto) throws RestException {

        int result = authService.updateUserPassword(userDto);
        return new Response.ResponseBuilder<Integer>( result == 1 ? "비밀번호 변경 완료" : "비밀번호 변경 실패" , result == 1 ? 200 : 403)
                .resultResponse(result)
                .total(result)
                .build();
    }
    /*
     *  로그인
     * */
    @PostMapping(path = "/signin")
    @ApiOperation(value = "로그인")
    public Response<AccessTokenResponse> authenticateUser(@RequestBody @ApiParam(value = "회원 가입 유저 정보") UserDto userDto) {
        return new Response.ResponseBuilder<AccessTokenResponse>( "로그인 성공" , 200)
                .resultResponse(authService.setAuth(userDto))
                .total(1)
                .build();
    }
}
