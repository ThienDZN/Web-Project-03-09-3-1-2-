package vn.iotstar.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.iotstar.config.SessionConstants;
import vn.iotstar.dto.UserProfileUpdateRequest;
import vn.iotstar.entity.UserAccount;
import vn.iotstar.service.IUserProfileService;
import vn.iotstar.service.impl.UserProfileServiceImpl;
import vn.iotstar.util.LocalImageStorage;
import vn.iotstar.validation.ValidationErrors;
import vn.iotstar.validation.ValidationUtils;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5L * 1024 * 1024,
        maxRequestSize = 6L * 1024 * 1024
)
@WebServlet("/profile")
public class ProfileController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final IUserProfileService userProfileService = new UserProfileServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount currentUser = requireCurrentUser(req, resp);
        if (currentUser == null) {
            return;
        }

        UserAccount freshUser = userProfileService.findById(currentUser.getUserId());
        req.getSession().setAttribute(SessionConstants.CURRENT_USER, freshUser);
        req.setAttribute("profileUser", freshUser);
        req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount currentUser = requireCurrentUser(req, resp);
        if (currentUser == null) {
            return;
        }

        UserAccount persistedUser = userProfileService.findById(currentUser.getUserId());
        ValidationErrors errors = validateProfileInput(req);
        req.setAttribute("errors", errors.asMap());
        if (errors.hasErrors()) {
            req.setAttribute("profileUser", persistedUser);
            bindSubmittedForm(req);
            req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
            return;
        }

        String uploadedImage = null;
        try {
            Part imagePart = req.getPart("imageFile");
            String nextImage = persistedUser.getImages();
            if (LocalImageStorage.hasUpload(imagePart)) {
                uploadedImage = LocalImageStorage.storeImage(imagePart, "profile");
                nextImage = uploadedImage;
            }

            UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest();
            updateRequest.setFullName(ValidationUtils.trimToNull(req.getParameter("fullName")));
            updateRequest.setPhone(ValidationUtils.trimToNull(req.getParameter("phone")));
            updateRequest.setImage(nextImage);

            UserAccount updatedUser = userProfileService.updateProfile(persistedUser.getUserId(), updateRequest);
            cleanupOldImage(persistedUser.getImages(), uploadedImage);

            req.getSession().setAttribute(SessionConstants.CURRENT_USER, updatedUser);
            resp.sendRedirect(req.getContextPath() + "/profile?message=" + encode("Profile updated successfully."));
        } catch (IllegalArgumentException e) {
            rollbackUpload(uploadedImage);
            ValidationErrors uploadErrors = new ValidationErrors();
            uploadErrors.add("imageFile", e.getMessage());
            req.setAttribute("error", e.getMessage());
            req.setAttribute("errors", uploadErrors.asMap());
            req.setAttribute("profileUser", persistedUser);
            bindSubmittedForm(req);
            req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
        } catch (Exception e) {
            rollbackUpload(uploadedImage);
            req.setAttribute("error", e.getMessage());
            req.setAttribute("profileUser", persistedUser);
            bindSubmittedForm(req);
            req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
        }
    }

    private ValidationErrors validateProfileInput(HttpServletRequest req) {
        ValidationErrors errors = new ValidationErrors();
        String fullName = ValidationUtils.trimToNull(req.getParameter("fullName"));
        if (fullName == null) {
            errors.add("fullName", "Please enter your full name.");
        } else if (ValidationUtils.exceedsLength(fullName, 120)) {
            errors.add("fullName", "Full name must not exceed 120 characters.");
        }

        String phone = ValidationUtils.trimToNull(req.getParameter("phone"));
        if (ValidationUtils.exceedsLength(phone, 20)) {
            errors.add("phone", "Phone number must not exceed 20 characters.");
        } else if (!ValidationUtils.isValidPhone(phone)) {
            errors.add("phone", "Phone number may only contain digits, spaces, plus, dash, or parentheses.");
        }
        return errors;
    }

    private UserAccount requireCurrentUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        UserAccount currentUser = session == null ? null : (UserAccount) session.getAttribute(SessionConstants.CURRENT_USER);
        if (currentUser != null) {
            return currentUser;
        }
        resp.sendRedirect(req.getContextPath() + "/login?message="
                + encode("Please log in before updating your profile."));
        return null;
    }

    private void cleanupOldImage(String previousImage, String uploadedImage) {
        if (uploadedImage == null || !LocalImageStorage.isLocalFile(previousImage)) {
            return;
        }
        try {
            LocalImageStorage.deleteIfExists(previousImage);
        } catch (IOException ignored) {
        }
    }

    private void rollbackUpload(String uploadedImage) {
        if (uploadedImage == null) {
            return;
        }
        try {
            LocalImageStorage.deleteIfExists(uploadedImage);
        } catch (IOException ignored) {
        }
    }

    private void bindSubmittedForm(HttpServletRequest req) {
        req.setAttribute("formFullName", submittedValue(req, "fullName"));
        req.setAttribute("formPhone", submittedValue(req, "phone"));
    }

    private String submittedValue(HttpServletRequest req, String fieldName) {
        if (!req.getParameterMap().containsKey(fieldName)) {
            return null;
        }
        String value = ValidationUtils.trimToNull(req.getParameter(fieldName));
        return value == null ? "" : value;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
