package vn.iotstar.service;

import vn.iotstar.dto.UserProfileUpdateRequest;
import vn.iotstar.entity.UserAccount;

public interface IUserProfileService {
    UserAccount findById(Long userId);
    UserAccount updateProfile(Long userId, UserProfileUpdateRequest request);
}
