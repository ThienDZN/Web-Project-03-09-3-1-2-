<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Reset Password</title>
    <link rel="stylesheet" href="<c:url value='/assets/app-theme.css'/>">
</head>
<body class="theme-music">
<div class="theme-shell">
    <div class="panel form-shell">
        <div class="eyebrow">Reset Password</div>
        <h1>Create a new password</h1>
        <p class="inline-note">This page is only available after the password-reset OTP has been verified successfully.</p>
        <c:if test="${not empty error}"><div class="error-box">${error}</div></c:if>
        <form method="post" action="<c:url value='/reset-password'/>">
            <input type="hidden" name="email" value="<c:out value='${email}'/>">
            <div class="form-group">
                <label>New Password</label>
                <input class="form-input${not empty errors.password ? ' is-invalid' : ''}" type="password" name="password" placeholder="At least 6 characters">
                <c:if test="${not empty errors.password}">
                    <div class="invalid-feedback d-block">${errors.password}</div>
                </c:if>
            </div>
            <div class="form-group">
                <label>Confirm New Password</label>
                <input class="form-input${not empty errors.confirmPassword ? ' is-invalid' : ''}" type="password" name="confirmPassword" placeholder="Retype password">
                <c:if test="${not empty errors.confirmPassword}">
                    <div class="invalid-feedback d-block">${errors.confirmPassword}</div>
                </c:if>
            </div>
            <div class="form-actions">
                <button class="btn btn-primary" type="submit">Update Password</button>
                <a class="btn btn-secondary" href="<c:url value='/login'/>">Back to Login</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>
