<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <link rel="stylesheet" href="<c:url value='/assets/app-theme.css'/>">
</head>
<body class="theme-music">
<div class="theme-shell">
    <div class="panel form-shell">
        <div class="eyebrow">Authentication</div>
        <h1>Login to continue</h1>
        <p class="inline-note">Sign in with your username or email. The account must be activated by OTP before login.</p>
        <c:if test="${not empty param.message}"><div class="message-box">${param.message}</div></c:if>
        <c:if test="${not empty error}"><div class="error-box">${error}</div></c:if>
        <form method="post" action="<c:url value='/login'/>">
            <div class="form-group">
                <label>Username or Email</label>
                <input class="form-input${not empty errors.usernameOrEmail ? ' is-invalid' : ''}" type="text" name="usernameOrEmail"
                       value="<c:out value='${not empty formData.usernameOrEmail ? formData.usernameOrEmail : param.usernameOrEmail}'/>"
                       placeholder="admin or email@example.com">
                <c:if test="${not empty errors.usernameOrEmail}">
                    <div class="invalid-feedback d-block">${errors.usernameOrEmail}</div>
                </c:if>
            </div>
            <div class="form-group">
                <label>Password</label>
                <input class="form-input${not empty errors.password ? ' is-invalid' : ''}" type="password" name="password" placeholder="Enter your password">
                <c:if test="${not empty errors.password}">
                    <div class="invalid-feedback d-block">${errors.password}</div>
                </c:if>
            </div>
            <div class="form-actions">
                <button class="btn btn-primary" type="submit">Login</button>
                <a class="btn btn-secondary" href="<c:url value='/home'/>">Back to Home</a>
            </div>
        </form>
        <div class="cta-row">
            <a class="btn btn-secondary" href="<c:url value='/register'/>">Create Account</a>
            <a class="btn btn-secondary" href="<c:url value='/forgot-password'/>">Forgot Password</a>
        </div>
        <p class="inline-note">Demo profile user: <strong>thien</strong> / <strong>User123@Aa1</strong></p>
    </div>
</div>
</body>
</html>
