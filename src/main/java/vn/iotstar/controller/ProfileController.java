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
        String uploadedImage = null;
        try {
            Part imagePart = req.getPart("imageFile");
            String nextImage = persistedUser.getImages();
            if (LocalImageStorage.hasUpload(imagePart)) {
                uploadedImage = LocalImageStorage.storeImage(imagePart, "profile");
                nextImage = uploadedImage;
            }

            UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest();
            updateRequest.setFullName(req.getParameter("fullName"));
            updateRequest.setPhone(req.getParameter("phone"));
            updateRequest.setImage(nextImage);

            UserAccount updatedUser = userProfileService.updateProfile(persistedUser.getUserId(), updateRequest);
            cleanupOldImage(persistedUser.getImages(), uploadedImage);

            req.getSession().setAttribute(SessionConstants.CURRENT_USER, updatedUser);
            resp.sendRedirect(req.getContextPath() + "/profile?message=" + encode("Profile updated successfully."));
        } catch (Exception e) {
            rollbackUpload(uploadedImage);
            req.setAttribute("error", e.getMessage());
            req.setAttribute("profileUser", mergeInput(persistedUser, req));
            req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
        }
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

    private UserAccount mergeInput(UserAccount persistedUser, HttpServletRequest req) {
        UserAccount fallback = persistedUser == null ? new UserAccount() : persistedUser;
        fallback.setFullName(valueOrDefault(req.getParameter("fullName"), fallback.getFullName()));
        fallback.setPhone(normalize(req.getParameter("phone")));
        return fallback;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String valueOrDefault(String value, String defaultValue) {
        String normalized = normalize(value);
        return normalized == null ? defaultValue : normalized;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
