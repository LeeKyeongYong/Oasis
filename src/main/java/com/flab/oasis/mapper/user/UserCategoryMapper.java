package com.flab.oasis.mapper.user;

import com.flab.oasis.model.OverlappingCategoryUserSelect;
import com.flab.oasis.model.UserCategoryCountSelect;
import com.flab.oasis.model.UserCategory;
import com.flab.oasis.model.UserCategoryCount;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserCategoryMapper {
    public void createUserCategory(List<UserCategory> userCategoryList);
    public List<UserCategory> getUserCategoryListByUid(String uid);
    public List<String> getUidListWithOverlappingBookCategory(OverlappingCategoryUserSelect overlappingCategoryUserSelect);
    public List<UserCategoryCount> getUserCategoryCountList(UserCategoryCountSelect userCategoryCountSelect);
}
