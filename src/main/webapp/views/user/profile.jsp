<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Profile</title>
    <link rel="stylesheet" href="<c:url value='/assets/app-theme.css'/>">
</head>
<body class="theme-music">
<div class="theme-shell">
    <div class="theme-nav">
        <div class="theme-brand">
            User Profile
            <small>Requirement 3 only: update full name, phone, and avatar image with JPA plus multipart upload</small>
        </div>
        <div class="theme-nav-links">
            <a class="btn btn-secondary" href="<c:url value='/home'/>">Home</a>
            <a class="btn btn-secondary" href="<c:url value='/product'/>">Catalog</a>
            <c:if test="${sessionScope.currentUser != null and sessionScope.currentUser.roleName == 'ADMIN'}">
                <a class="btn btn-secondary" href="<c:url value='/admin/products'/>">Admin</a>
            </c:if>
            <a class="btn btn-primary" href="<c:url value='/logout'/>">Logout</a>
        </div>
    </div>

    <c:if test="${not empty param.message}">
        <div class="message-box">${param.message}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="error-box">${error}</div>
    </c:if>

    <section class="profile-layout">
        <div class="panel profile-card">
            <div class="eyebrow">Account Snapshot</div>
            <c:choose>
                <c:when test="${empty profileUser.images}">
                    <img class="profile-avatar" src="<c:url value='/assets/no-image.svg'/>" alt="No profile image">
                </c:when>
                <c:when test="${fn:startsWith(profileUser.images, 'http://') or fn:startsWith(profileUser.images, 'https://')}">
                    <img class="profile-avatar" src="${profileUser.images}" alt="${profileUser.fullName}">
                </c:when>
                <c:otherwise>
                    <img class="profile-avatar" src="<c:url value='/image?fname=${profileUser.images}'/>" alt="${profileUser.fullName}">
                </c:otherwise>
            </c:choose>

            <h1 style="margin:0 0 10px;">${profileUser.fullName}</h1>
            <p class="hero-lead" style="font-size:16px; max-width:none;">
                This module is kept separate from login, OTP, and admin CRUD so you can wrap it with SiteMesh or add form validation later without reworking the profile logic.
            </p>

            <div class="profile-meta-list">
                <div class="profile-meta-item">
                    <strong>Username</strong>
                    <span>${profileUser.username}</span>
                </div>
                <div class="profile-meta-item">
                    <strong>Email</strong>
                    <span>${profileUser.email}</span>
                </div>
                <div class="profile-meta-item">
                    <strong>Phone</strong>
                    <span><c:out value="${empty profileUser.phone ? 'Not updated yet' : profileUser.phone}"/></span>
                </div>
                <div class="profile-meta-item">
                    <strong>Role</strong>
                    <span>${profileUser.roleName}</span>
                </div>
            </div>
        </div>

        <div class="panel profile-card">
            <div class="eyebrow">Edit Profile</div>
            <h2 style="margin-top:10px;">Update your personal information</h2>

            <form method="post" action="<c:url value='/profile'/>" enctype="multipart/form-data">
                <div class="form-group">
                    <label>Full Name</label>
                    <input class="form-input${not empty errors.fullName ? ' is-invalid' : ''}" type="text" name="fullName"
                           value="<c:out value='${formFullName != null ? formFullName : profileUser.fullName}'/>" placeholder="Enter your full name">
                    <c:if test="${not empty errors.fullName}">
                        <div class="invalid-feedback d-block">${errors.fullName}</div>
                    </c:if>
                </div>

                <div class="form-group">
                    <label>Phone</label>
                    <input class="form-input${not empty errors.phone ? ' is-invalid' : ''}" type="text" name="phone"
                           value="<c:out value='${formPhone != null ? formPhone : profileUser.phone}'/>" placeholder="Enter your phone number">
                    <div class="field-hint">You can leave this field empty if you do not want to show a phone number yet.</div>
                    <c:if test="${not empty errors.phone}">
                        <div class="invalid-feedback d-block">${errors.phone}</div>
                    </c:if>
                </div>

                <div class="form-group">
                    <label>Username</label>
                    <input class="form-input" type="text" value="<c:out value='${profileUser.username}'/>" readonly>
                </div>

                <div class="form-group">
                    <label>Email</label>
                    <input class="form-input" type="text" value="<c:out value='${profileUser.email}'/>" readonly>
                </div>

                <div class="form-group">
                    <label>Profile Image</label>
                    <input class="form-file${not empty errors.imageFile ? ' is-invalid' : ''}" type="file" name="imageFile" accept=".jpg,.jpeg,.png,.gif,.webp,.bmp,image/*">
                    <div class="field-hint">If you do not choose a new file, the current profile image will be kept.</div>
                    <c:if test="${not empty errors.imageFile}">
                        <div class="invalid-feedback d-block">${errors.imageFile}</div>
                    </c:if>
                </div>

                <div class="form-actions">
                    <button class="btn btn-primary" type="submit">Update Profile</button>
                    <a class="btn btn-secondary" href="<c:url value='/home'/>">Back to Home</a>
                    <c:if test="${sessionScope.currentUser != null and sessionScope.currentUser.roleName == 'ADMIN'}">
                        <a class="btn btn-secondary" href="<c:url value='/admin/products'/>">Admin Dashboard</a>
                    </c:if>
                </div>
            </form>
        </div>
    </section>
</div>
</body>
</html>
