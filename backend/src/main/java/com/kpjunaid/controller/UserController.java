package com.kpjunaid.controller;

import com.kpjunaid.common.AppConstants;
import com.kpjunaid.common.UserPrincipal;
import com.kpjunaid.dto.*;
import com.kpjunaid.entity.Subject;
import com.kpjunaid.entity.User;
import com.kpjunaid.repository.UserRepository;
import com.kpjunaid.response.PostResponse;
import com.kpjunaid.response.UserResponse;
import com.kpjunaid.service.JwtTokenService;
import com.kpjunaid.service.PostService;
import com.kpjunaid.service.SubjectService;
import com.kpjunaid.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SubjectService subjectService;
    private final PostService postService;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    
        @PostMapping("/signup")
        public ResponseEntity<?> signup(@RequestBody @Valid SignupDto signupDto) {
            User savedUser = userService.createNewUser(signupDto);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        }
    
        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
    
    
            System.out.println("Dans usercontroller" + loginDto.getEmail());
            // le study group est le dernier elemetn de la chaine de caractere
            String studygroup = loginDto.getEmail().substring(loginDto.getEmail().length() - 1);
    
            String resultat = loginDto.getEmail().substring(0, loginDto.getEmail().length() -1 );
            loginDto.setEmail(resultat);
            Subject savedSubject = subjectService.createNewSubject(studygroup);
            System.out.println("Dans usercontroller : " + resultat);
            User loginUser = userService.getUserByEmail(resultat);
    
            System.out.println("Dans usercontroller : " + loginUser.getAlreadyused());
            System.out.println("Dans usercontroller : " + loginUser.getFirstName());
            System.out.println("Dans usercontroller : " + loginUser.getCoverPhoto());
            System.out.println("Dans usercontroller : " + loginUser.getReportExp());
    
            if (loginUser.getAlreadyused() != false) {
                return new ResponseEntity<>("User already used", HttpStatus.BAD_REQUEST);
            }
            else {
                System.out.println("Dans usercontroller ON EST LA LA TEAM : ");
                loginUser.setAlreadyused(true);
                loginUser.setStudyGroup(studygroup);
                userRepository.save(loginUser);
            UserPrincipal userPrincipal = new UserPrincipal(loginUser);
            HttpHeaders newHttpHeaders = new HttpHeaders();
            newHttpHeaders.add(AppConstants.TOKEN_HEADER, jwtTokenService.generateToken(userPrincipal));
            
            int id = 0;
            LoginResponseDto response = new LoginResponseDto(id, loginUser, studygroup);
            System.out.println("Dans usercontroller ON EST LA LA TEAM : ");
            
            return new ResponseEntity<>(response, newHttpHeaders, HttpStatus.OK);
        }
        



       
    }

    @GetMapping("/profile")
    public ResponseEntity<?> showUserProfile(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getPrincipal().toString());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/account/update/info")
    public ResponseEntity<?> updateUserInfo(@RequestBody @Valid UpdateUserInfoDto updateUserInfoDto) {
        System.out.println("Dans usercontroller" + updateUserInfoDto.getReportExp());
        User updatedUser = userService.updateUserInfo(updateUserInfoDto);
        
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PostMapping("/account/update/email")
    public ResponseEntity<?> updateUserEmail(@RequestBody @Valid UpdateEmailDto updateEmailDto) {
        userService.updateEmail(updateEmailDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/account/update/password")
    public ResponseEntity<?> updateUserPassword(@RequestBody @Valid UpdatePasswordDto updatePasswordDto) {
        userService.updatePassword(updatePasswordDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/account/update/profile-photo")
    public ResponseEntity<?> updateProfilePhoto(@RequestParam("profilePhoto") MultipartFile profilePhoto) {
        User updatedUser = userService.updateProfilePhoto(profilePhoto);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PostMapping("/account/update/cover-photo")
    public ResponseEntity<?> updateCoverPhoto(@RequestParam("coverPhoto") MultipartFile coverPhoto) {
        User updatedUser = userService.updateCoverPhoto(coverPhoto);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PostMapping("/account/delete")
    public ResponseEntity<?> deleteUserAccount() {
        userService.deleteUserAccount();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/account/follow/{userId}")
    public ResponseEntity<?> followUser(@PathVariable("userId") Long userId) {
        userService.followUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/account/unfollow/{userId}")
    public ResponseEntity<?> unfollowUser(@PathVariable("userId") Long userId) {
        userService.unfollowUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/following")
    public ResponseEntity<?> getUserFollowingUsers(@PathVariable("userId") Long userId,
                                                  @RequestParam("page") Integer page,
                                                  @RequestParam("size") Integer size) {
        page = page < 0 ? 0 : page-1;
        size = size <= 0 ? 5 : size;
        List<UserResponse> followingList = userService.getFollowingUsersPaginate(userId, page, size);
        return new ResponseEntity<>(followingList, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/follower")
    public ResponseEntity<?> getUserFollowerUsers(@PathVariable("userId") Long userId,
                                                 @RequestParam("page") Integer page,
                                                 @RequestParam("size") Integer size) {
        page = page < 0 ? 0 : page-1;
        size = size <= 0 ? 5 : size;
        List<UserResponse> followingList = userService.getFollowerUsersPaginate(userId, page, size);
        return new ResponseEntity<>(followingList, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable("userId") Long userId) {
        User authUser = userService.getAuthenticatedUser();
        User targetUser = userService.getUserById(userId);
        UserResponse userResponse = UserResponse.builder()
                .user(targetUser)
                .followedByAuthUser(targetUser.getFollowerUsers().contains(authUser))
                .build();
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<?> getUserPosts(@PathVariable("userId") Long userId,
                                          @RequestParam("page") Integer page,
                                          @RequestParam("size") Integer size) {
        page = page < 0 ? 0 : page-1;
        size = size <= 0 ? 5 : size;
        User targetUser = userService.getUserById(userId);
        List<PostResponse> userPosts = postService.getPostsByUserPaginate(targetUser, page, size);
        return new ResponseEntity<>(userPosts, HttpStatus.OK);
    }

    @GetMapping("/users/search")
    public ResponseEntity<?> searchUser(@RequestParam("key") String key,
                                        @RequestParam("page") Integer page,
                                        @RequestParam("size") Integer size) {
        page = page < 0 ? 0 : page-1;
        size = size <= 0 ? 5 : size;
        List<UserResponse> userSearchResult = userService.getUserSearchResult(key, page, size);
        return new ResponseEntity<>(userSearchResult, HttpStatus.OK);
    }

    @PostMapping("/verify-email/{token}")
    public ResponseEntity<?> verifyEmail(@PathVariable("token") String token) {
        userService.verifyEmail(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        userService.forgotPassword(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reset-password/{token}")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto,
                                           @PathVariable("token") String token) {
        userService.resetPassword(token, resetPasswordDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
