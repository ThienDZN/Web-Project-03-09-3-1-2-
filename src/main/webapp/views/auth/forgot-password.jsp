<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Forgot Password</title>
    <link rel="stylesheet" href="<c:url value='/assets/app-theme.css'/>">
</head>
<body class="theme-music">
<div class="theme-shell">
    <div class="panel form-shell">
        <div class="eyebrow">Password Recovery</div>
        <h1>Forgot your password?</h1>
        <p class="inline-note">Enter the registered email address to receive an OTP for password reset verification.</p>
        <c:if test="${not empty param.message}"><div class="message-box">${param.message}</div></c:if>
        <c:if test="${not empty error}"><div class="error-box">${error}</div></c:if>
        <form method="post" action="<c:url value='/forgot-password'/>">
            <div class="form-group">
                <label>Registered Email</label>
                <input class="form-input${not empty errors.email ? ' is-invalid' : ''}" type="email" name="email"
                       value="<c:out value='${not empty formData.email ? formData.email : param.email}'/>" placeholder="email@example.com">
                <c:if test="${not empty errors.email}">
                    <div class="invalid-feedback d-block">${errors.email}</div>
                </c:if>
            </div>
            <div class="form-actions">
                <button class="btn btn-primary" type="submit">Send Reset OTP</button>
                <a class="btn btn-secondary" href="<c:url value='/login'/>">Back to Login</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>
