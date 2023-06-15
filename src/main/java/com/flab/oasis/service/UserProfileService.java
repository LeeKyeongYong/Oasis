package com.flab.oasis.service;

import com.flab.oasis.model.GeneralResponse;
import com.flab.oasis.model.UserCategory;
import com.flab.oasis.model.UserInfo;
import com.flab.oasis.model.UserProfile;
import com.flab.oasis.model.exception.NotFoundException;
import com.flab.oasis.repository.UserCategoryRepository;
import com.flab.oasis.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserInfoRepository userInfoRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final UserAuthService userAuthService;

    public GeneralResponse<Boolean> isExistsNickname(String nickname) {
        return GeneralResponse.<Boolean>builder()
                .code(0)
                .data(userInfoRepository.isExistsNickname(nickname))
                .build();
    }

    @Transactional
    public GeneralResponse<Boolean> createUserProfile(UserProfile userProfile) {
        String uid = userAuthService.getAuthenticatedUid();

        userInfoRepository.createUserInfo(
                UserInfo.builder()
                        .uid(uid)
                        .nickname(userProfile.getNickname())
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

        return GeneralResponse.<Boolean>builder()
                .code(0)
                .data(true)
                .build();
    }

    public GeneralResponse<UserProfile> getUserProfileByUid() {
        try {
            String uid = userAuthService.getAuthenticatedUid();
            UserInfo userInfo = userInfoRepository.getUserInfoByUid(uid);
            List<UserCategory> userCategoryList = userCategoryRepository.getUserCategoryListByUid(uid);

            UserProfile userProfile = UserProfile.builder()
                    .uid(uid)
                    .nickname(userInfo.getNickname())
                    .introduce(userInfo.getIntroduce())
                    .bookCategoryList(
                            userCategoryList.stream()
                                    .map(UserCategory::getBookCategory)
                                    .collect(Collectors.toList())
                    )
                    .build();

            return GeneralResponse.<UserProfile>builder()
                    .code(0)
                    .data(userProfile)
                    .build();
        } catch (NotFoundException e) {
            return GeneralResponse.<UserProfile>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        }
    }
}
