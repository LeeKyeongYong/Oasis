package com.flab.oasis.service;

import com.flab.oasis.model.SQLResultResponse;
import com.flab.oasis.model.UserCategory;
import com.flab.oasis.model.UserInfo;
import com.flab.oasis.model.UserProfile;
import com.flab.oasis.repository.UserCategoryRepository;
import com.flab.oasis.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserInfoRepository userInfoRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final UserAuthService userAuthService;

    public boolean isExistsNickname(String nickname) {
        return userInfoRepository.isExistsNickname(nickname);
    }

    @Transactional
    public SQLResultResponse createUserProfile(UserProfile userProfile) {
        String uid = userAuthService.getAuthenticatedUid();

        try {
            userInfoRepository.createUserInfo(
                    UserInfo.builder()
                            .uid(uid)
                            .nickname(null)
                            .introduce(userProfile.getIntroduce())
                            .build()
            );

            if (!userProfile.getBookCategoryList().isEmpty()) {
                userCategoryRepository.createUserCategory(
                        userProfile.getBookCategoryList().stream()
                                .map(
                                        bookCategory -> UserCategory.builder()
                                                .uid(uid)
                                                .bookCategory(bookCategory)
                                                .build()
                                )
                                .collect(Collectors.toList())
                );
            }

            return SQLResultResponse.builder()
                    .result(true)
                    .message("success")
                    .build();
        } catch (SQLException e) {
            return SQLResultResponse.builder()
                    .result(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    public UserProfile getUserProfileByUid() {
        String uid = userAuthService.getAuthenticatedUid();
        UserInfo userInfo = userInfoRepository.getUserInfoByUid(uid);
        List<UserCategory> userCategoryList = userCategoryRepository.getUserCategoryListByUid(uid);

        return UserProfile.builder()
                .uid(uid)
                .nickname(userInfo.getNickname())
                .introduce(userInfo.getIntroduce())
                .bookCategoryList(
                        userCategoryList.stream()
                                .map(UserCategory::getBookCategory)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
