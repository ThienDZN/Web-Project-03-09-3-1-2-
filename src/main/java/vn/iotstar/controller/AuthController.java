package vn.iotstar.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.config.OtpPurpose;
import vn.iotstar.config.SessionConstants;
import vn.iotstar.entity.UserAccount;
import vn.iotstar.service.IAuthService;
import vn.iotstar.service.impl.AuthServiceImpl;
import vn.iotstar.validation.ValidationErrors;
import vn.iotstar.validation.ValidationUtils;

@WebServlet(urlPatterns = {
        "/login",
        "/logout",
        "/register",
        "/verify-otp",
        "/forgot-password",
        "/reset-password",
        "/resend-otp"
})
public class AuthController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final IAuthService authService = new AuthServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        if ("/logout".equals(servletPath)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/home?message=" + encode("You have been logged out successfully."));
            return;
        }
        if ("/login".equals(servletPath)) {
            forwardLoginView(req, resp);
            return;
        }
        if ("/register".equals(servletPath)) {
            forwardRegisterView(req, resp);
            return;
        }
        if ("/verify-otp".equals(servletPath)) {
            req.setAttribute("purpose", valueOrDefault(req.getParameter("purpose"), OtpPurpose.REGISTER));
            req.setAttribute("email", valueOrDefault(req.getParameter("email"), ""));
            forwardVerifyOtpView(req, resp);
            return;
        }
        if ("/forgot-password".equals(servletPath)) {
            forwardForgotPasswordView(req, resp);
            return;
        }
        if ("/reset-password".equals(servletPath)) {
            String email = valueOrDefault(req.getParameter("email"), "");
            HttpSession session = req.getSession(false);
            String allowedEmail = session == null ? null : (String) session.getAttribute(SessionConstants.RESET_PASSWORD_EMAIL);
            if (allowedEmail == null || !allowedEmail.equalsIgnoreCase(email)) {
                resp.sendRedirect(req.getContextPath() + "/forgot-password?message=" + encode("Please verify the OTP before resetting your password."));
                return;
            }
            req.setAttribute("email", email);
            forwardResetPasswordView(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        try {
            if ("/login".equals(servletPath)) {
                handleLogin(req, resp);
                return;
            }
            if ("/register".equals(servletPath)) {
                handleRegister(req, resp);
                return;
            }
            if ("/verify-otp".equals(servletPath)) {
                handleVerifyOtp(req, resp);
                return;
            }
            if ("/forgot-password".equals(servletPath)) {
                handleForgotPassword(req, resp);
                return;
            }
            if ("/reset-password".equals(servletPath)) {
                handleResetPassword(req, resp);
                return;
            }
            if ("/resend-otp".equals(servletPath)) {
                handleResendOtp(req, resp);
                return;
            }
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            forwardFormView(req, resp, servletPath);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> formData = new LinkedHashMap<>();
        ValidationErrors errors = validateLogin(req, formData);
        bindFormState(req, formData, errors);
        if (errors.hasErrors()) {
            forwardLoginView(req, resp);
            return;
        }

        UserAccount user = authService.login(formData.get("usernameOrEmail"), req.getParameter("password"));
        req.getSession(true).setAttribute(SessionConstants.CURRENT_USER, user);
        if ("ADMIN".equalsIgnoreCase(user.getRoleName())) {
            resp.sendRedirect(req.getContextPath() + "/admin/products?message=" + encode("Admin login successful."));
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/home?message=" + encode("Login successful."));
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> formData = new LinkedHashMap<>();
        ValidationErrors errors = validateRegister(req, formData);
        bindFormState(req, formData, errors);
        if (errors.hasErrors()) {
            forwardRegisterView(req, resp);
            return;
        }

        String deliveryMessage = authService.register(
                formData.get("fullName"),
                formData.get("username"),
                formData.get("email"),
                req.getParameter("password"),
                req.getParameter("confirmPassword"));
        resp.sendRedirect(req.getContextPath() + "/verify-otp?purpose=" + OtpPurpose.REGISTER
                + "&email=" + encode(formData.get("email"))
                + "&message=" + encode(deliveryMessage));
    }

    private void handleVerifyOtp(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String purpose = valueOrDefault(req.getParameter("purpose"), OtpPurpose.REGISTER);
        String email = ValidationUtils.normalizeEmail(req.getParameter("email"));
        Map<String, String> formData = new LinkedHashMap<>();
        ValidationErrors errors = validateOtp(req, purpose, email, formData);
        req.setAttribute("purpose", purpose);
        req.setAttribute("email", ValidationUtils.emptyIfNull(email));
        bindFormState(req, formData, errors);
        if (errors.hasErrors()) {
            forwardVerifyOtpView(req, resp);
            return;
        }

        String otp = formData.get("otp");
        if (OtpPurpose.RESET_PASSWORD.equalsIgnoreCase(purpose)) {
            authService.verifyResetPasswordOtp(email, otp);
            req.getSession(true).setAttribute(SessionConstants.RESET_PASSWORD_EMAIL, email);
            resp.sendRedirect(req.getContextPath() + "/reset-password?email=" + encode(email));
            return;
        }
        authService.verifyRegistrationOtp(email, otp);
        resp.sendRedirect(req.getContextPath() + "/login?message=" + encode("Your account has been activated. You can log in now."));
    }

    private void handleForgotPassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> formData = new LinkedHashMap<>();
        ValidationErrors errors = validateEmailOnly(req, formData, "email", "Please enter your email address.");
        bindFormState(req, formData, errors);
        if (errors.hasErrors()) {
            forwardForgotPasswordView(req, resp);
            return;
        }

        String deliveryMessage = authService.sendResetPasswordOtp(formData.get("email"));
        resp.sendRedirect(req.getContextPath() + "/verify-otp?purpose=" + OtpPurpose.RESET_PASSWORD
                + "&email=" + encode(formData.get("email"))
                + "&message=" + encode(deliveryMessage));
    }

    private void handleResetPassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = ValidationUtils.normalizeEmail(req.getParameter("email"));
        req.setAttribute("email", ValidationUtils.emptyIfNull(email));
        if (!ValidationUtils.isValidEmail(email)) {
            req.setAttribute("error", "Reset request is not valid.");
            forwardResetPasswordView(req, resp);
            return;
        }

        Map<String, String> formData = new LinkedHashMap<>();
        ValidationErrors errors = validatePasswordReset(req, formData);
        bindFormState(req, formData, errors);
        if (errors.hasErrors()) {
            forwardResetPasswordView(req, resp);
            return;
        }

        authService.resetPassword(email, req.getParameter("password"), req.getParameter("confirmPassword"));
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.removeAttribute(SessionConstants.RESET_PASSWORD_EMAIL);
        }
        resp.sendRedirect(req.getContextPath() + "/login?message=" + encode("Password updated successfully. Please log in again."));
    }

    private void handleResendOtp(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> formData = new LinkedHashMap<>();
        ValidationErrors errors = validateEmailOnly(req, formData, "email", "Please enter your email address.");
        req.setAttribute("purpose", OtpPurpose.REGISTER);
        req.setAttribute("email", ValidationUtils.emptyIfNull(formData.get("email")));
        bindFormState(req, formData, errors);
        if (errors.hasErrors()) {
            forwardVerifyOtpView(req, resp);
            return;
        }

        String deliveryMessage = authService.resendRegistrationOtp(formData.get("email"));
        resp.sendRedirect(req.getContextPath() + "/verify-otp?purpose=" + OtpPurpose.REGISTER
                + "&email=" + encode(formData.get("email"))
                + "&message=" + encode(deliveryMessage));
    }

    private ValidationErrors validateLogin(HttpServletRequest req, Map<String, String> formData) {
        ValidationErrors errors = new ValidationErrors();
        String usernameOrEmail = ValidationUtils.trimToNull(req.getParameter("usernameOrEmail"));
        formData.put("usernameOrEmail", ValidationUtils.emptyIfNull(usernameOrEmail));
        if (usernameOrEmail == null) {
            errors.add("usernameOrEmail", "Please enter your username or email.");
        } else if (ValidationUtils.exceedsLength(usernameOrEmail, 120)) {
            errors.add("usernameOrEmail", "Username or email must not exceed 120 characters.");
        }

        String password = req.getParameter("password");
        if (password == null || password.isBlank()) {
            errors.add("password", "Please enter your password.");
        }
        return errors;
    }

    private ValidationErrors validateRegister(HttpServletRequest req, Map<String, String> formData) {
        ValidationErrors errors = new ValidationErrors();

        String fullName = ValidationUtils.trimToNull(req.getParameter("fullName"));
        formData.put("fullName", ValidationUtils.emptyIfNull(fullName));
        if (fullName == null) {
            errors.add("fullName", "Please enter your full name.");
        } else if (ValidationUtils.exceedsLength(fullName, 120)) {
            errors.add("fullName", "Full name must not exceed 120 characters.");
        }

        String username = ValidationUtils.trimToNull(req.getParameter("username"));
        formData.put("username", ValidationUtils.emptyIfNull(username));
        if (username == null) {
            errors.add("username", "Please enter a username.");
        } else if (!ValidationUtils.isValidUsername(username)) {
            errors.add("username", "Username must be 3-50 characters and only contain letters, numbers, dot, dash, or underscore.");
        }

        String email = ValidationUtils.normalizeEmail(req.getParameter("email"));
        formData.put("email", ValidationUtils.emptyIfNull(email));
        if (email == null) {
            errors.add("email", "Please enter your email address.");
        } else if (!ValidationUtils.isValidEmail(email)) {
            errors.add("email", "Please enter a valid email address.");
        }

        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        if (password == null || password.isBlank()) {
            errors.add("password", "Please enter a password.");
        } else if (password.length() < 6) {
            errors.add("password", "Password must contain at least 6 characters.");
        }

        if (confirmPassword == null || confirmPassword.isBlank()) {
            errors.add("confirmPassword", "Please confirm your password.");
        } else if (password != null && !password.equals(confirmPassword)) {
            errors.add("confirmPassword", "Password confirmation does not match.");
        }
        return errors;
    }

    private ValidationErrors validateOtp(HttpServletRequest req, String purpose, String email, Map<String, String> formData) {
        ValidationErrors errors = new ValidationErrors();
        String otp = ValidationUtils.trimToNull(req.getParameter("otp"));
        formData.put("otp", ValidationUtils.emptyIfNull(otp));

        if (!OtpPurpose.REGISTER.equalsIgnoreCase(purpose) && !OtpPurpose.RESET_PASSWORD.equalsIgnoreCase(purpose)) {
            errors.add("otp", "Verification request is not valid.");
        }
        if (!ValidationUtils.isValidEmail(email)) {
            errors.add("otp", "Email for OTP verification is not valid.");
        }
        if (otp == null) {
            errors.add("otp", "Please enter the OTP code.");
        } else if (!ValidationUtils.isValidOtp(otp)) {
            errors.add("otp", "OTP must contain exactly 6 digits.");
        }
        return errors;
    }

    private ValidationErrors validateEmailOnly(HttpServletRequest req, Map<String, String> formData,
                                               String fieldName, String emptyMessage) {
        ValidationErrors errors = new ValidationErrors();
        String email = ValidationUtils.normalizeEmail(req.getParameter(fieldName));
        formData.put(fieldName, ValidationUtils.emptyIfNull(email));
        if (email == null) {
            errors.add(fieldName, emptyMessage);
        } else if (!ValidationUtils.isValidEmail(email)) {
            errors.add(fieldName, "Please enter a valid email address.");
        }
        return errors;
    }

    private ValidationErrors validatePasswordReset(HttpServletRequest req, Map<String, String> formData) {
        ValidationErrors errors = new ValidationErrors();
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        if (password == null || password.isBlank()) {
            errors.add("password", "Please enter a new password.");
        } else if (password.length() < 6) {
            errors.add("password", "Password must contain at least 6 characters.");
        }
        if (confirmPassword == null || confirmPassword.isBlank()) {
            errors.add("confirmPassword", "Please confirm the new password.");
        } else if (password != null && !password.equals(confirmPassword)) {
            errors.add("confirmPassword", "Password confirmation does not match.");
        }
        return errors;
    }

    private void bindFormState(HttpServletRequest req, Map<String, String> formData, ValidationErrors errors) {
        req.setAttribute("formData", formData);
        req.setAttribute("errors", errors.asMap());
    }

    private void forwardFormView(HttpServletRequest req, HttpServletResponse resp, String servletPath)
            throws ServletException, IOException {
        if ("/login".equals(servletPath)) {
            forwardLoginView(req, resp);
            return;
        }
        if ("/register".equals(servletPath)) {
            forwardRegisterView(req, resp);
            return;
        }
        if ("/verify-otp".equals(servletPath) || "/resend-otp".equals(servletPath)) {
            forwardVerifyOtpView(req, resp);
            return;
        }
        if ("/forgot-password".equals(servletPath)) {
            forwardForgotPasswordView(req, resp);
            return;
        }
        if ("/reset-password".equals(servletPath)) {
            forwardResetPasswordView(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void forwardLoginView(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
    }

    private void forwardRegisterView(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/views/auth/register.jsp").forward(req, resp);
    }

    private void forwardVerifyOtpView(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getAttribute("purpose") == null) {
            req.setAttribute("purpose", valueOrDefault(req.getParameter("purpose"), OtpPurpose.REGISTER));
        }
        if (req.getAttribute("email") == null) {
            req.setAttribute("email", valueOrDefault(req.getParameter("email"), ""));
        }
        req.getRequestDispatcher("/views/auth/verify-otp.jsp").forward(req, resp);
    }

    private void forwardForgotPasswordView(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(req, resp);
    }

    private void forwardResetPasswordView(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getAttribute("email") == null) {
            req.setAttribute("email", valueOrDefault(req.getParameter("email"), ""));
        }
        req.getRequestDispatcher("/views/auth/reset-password.jsp").forward(req, resp);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String valueOrDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
