package vn.iotstar.service.impl;

import vn.iotstar.dao.IUserAccountDao;
import vn.iotstar.dao.impl.UserAccountDao;
import vn.iotstar.dto.UserProfileUpdateRequest;
import vn.iotstar.entity.UserAccount;
import vn.iotstar.service.IUserProfileService;

public class UserProfileServiceImpl implements IUserProfileService {
    private final IUserAccountDao userAccountDao = new UserAccountDao();

    @Override
    public UserAccount findById(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Please log in again to access your profile.");
        }
        UserAccount user = userAccountDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("Unable to find your account.");
        }
        return user;
    }

    @Override
    public UserAccount updateProfile(Long userId, UserProfileUpdateRequest request) {
        UserAccount user = findById(userId);
        UserProfileUpdateRequest normalized = normalize(request);

        user.setFullName(normalized.getFullName());
        user.setPhone(normalized.getPhone());
        user.setImages(normalized.getImage());
        userAccountDao.update(user);

        return findById(userId);
    }

    private UserProfileUpdateRequest normalize(UserProfileUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Profile data must not be empty.");
        }

        UserProfileUpdateRequest normalized = new UserProfileUpdateRequest();
        normalized.setFullName(normalizeRequired(request.getFullName(), "Full name must not be empty.", 120));
        normalized.setPhone(normalizeOptional(request.getPhone(), 20, "Phone number must not exceed 20 characters."));
        normalized.setImage(normalizeOptional(request.getImage(), 500, "Image path must not exceed 500 characters."));
        return normalized;
    }

    private String normalizeRequired(String value, String emptyMessage, int maxLength) {
        String normalized = normalizeOptional(value, maxLength, "This field is too long.");
        if (normalized == null) {
            throw new IllegalArgumentException(emptyMessage);
        }
        return normalized;
    }

    private String normalizeOptional(String value, int maxLength, String tooLongMessage) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(tooLongMessage);
        }
        return trimmed;
    }
}
