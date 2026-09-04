<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register</title>
    <link rel="stylesheet" href="<c:url value='/assets/app-theme.css'/>">
</head>
<body class="theme-music">
<div class="theme-shell">
    <div class="panel form-shell">
        <div class="eyebrow">Register</div>
        <h1>Create a new account</h1>
        <p class="inline-note">After registration, the system sends an OTP by email so you can activate the account.</p>
        <c:if test="${not empty error}"><div class="error-box">${error}</div></c:if>
        <form method="post" action="<c:url value='/register'/>">
            <div class="form-group">
                <label>Full Name</label>
                <input class="form-input${not empty errors.fullName ? ' is-invalid' : ''}" type="text" name="fullName"
                       value="<c:out value='${not empty formData.fullName ? formData.fullName : param.fullName}'/>" placeholder="Your full name">
                <c:if test="${not empty errors.fullName}">
                    <div class="invalid-feedback d-block">${errors.fullName}</div>
                </c:if>
            </div>
            <div class="form-group">
                <label>Username</label>
                <input class="form-input${not empty errors.username ? ' is-invalid' : ''}" type="text" name="username"
                       value="<c:out value='${not empty formData.username ? formData.username : param.username}'/>" placeholder="your username">
                <c:if test="${not empty errors.username}">
                    <div class="invalid-feedback d-block">${errors.username}</div>
                </c:if>
            </div>
            <div class="form-group">
                <label>Email</label>
                <input class="form-input${not empty errors.email ? ' is-invalid' : ''}" type="email" name="email"
                       value="<c:out value='${not empty formData.email ? formData.email : param.email}'/>" placeholder="email@example.com">
                <c:if test="${not empty errors.email}">
                    <div class="invalid-feedback d-block">${errors.email}</div>
                </c:if>
            </div>
            <div class="form-group">
                <label>Password</label>
                <input class="form-input${not empty errors.password ? ' is-invalid' : ''}" type="password" name="password" placeholder="At least 6 characters">
                <c:if test="${not empty errors.password}">
                    <div class="invalid-feedback d-block">${errors.password}</div>
                </c:if>
            </div>
            <div class="form-group">
                <label>Confirm Password</label>
                <input class="form-input${not empty errors.confirmPassword ? ' is-invalid' : ''}" type="password" name="confirmPassword" placeholder="Retype password">
                <c:if test="${not empty errors.confirmPassword}">
                    <div class="invalid-feedback d-block">${errors.confirmPassword}</div>
                </c:if>
            </div>
            <div class="form-actions">
                <button class="btn btn-primary" type="submit">Register and Send OTP</button>
                <a class="btn btn-secondary" href="<c:url value='/login'/>">Back to Login</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>
