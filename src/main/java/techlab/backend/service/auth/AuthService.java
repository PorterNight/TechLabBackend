package techlab.backend.service.auth;

import techlab.backend.dto.security.*;

import java.util.List;

public interface AuthService {

    UserSignedUpResponseDto signUpUser(UserSignUpRequest usernamePasswordDto);

    UserSignedInResponseDto signInUser(UserSignInRequest usernamePasswordDto);

    String userLogOut(UserLogOut userLogOut);
    UserSecurityResponseDto updateUserInfo(UserInfoUpdateRequest userInfoUpdateRequest);

    List<UserSecurityResponseDto> getAllUsers(Long id1, Long id2);

    AccessRefreshTokenResponse issueAccessRefreshToken(String refreshToken);

}
