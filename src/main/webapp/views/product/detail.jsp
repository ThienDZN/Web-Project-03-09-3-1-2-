<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Track Detail</title>
    <link rel="stylesheet" href="<c:url value='/assets/app-theme.css'/>">
</head>
<body class="theme-music">
<div class="theme-shell">
    <div class="theme-nav">
        <div class="theme-brand">
            Track Detail
            <small>This page opens when the user clicks an entry from the homepage or the /product page</small>
        </div>
        <div class="theme-nav-links">
            <a class="btn btn-secondary" href="<c:url value='/home'/>">Home</a>
            <a class="btn btn-primary" href="<c:url value='/product'/>">Back to List</a>
            <c:if test="${sessionScope.currentUser != null and sessionScope.currentUser.roleName == 'ADMIN'}">
                <a class="btn btn-secondary" href="<c:url value='/admin/products'/>">Admin</a>
            </c:if>
            <c:choose>
                <c:when test="${sessionScope.currentUser != null}">
                    <a class="btn btn-secondary" href="<c:url value='/profile'/>">Profile</a>
                    <a class="btn btn-secondary" href="<c:url value='/logout'/>">Logout</a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-secondary" href="<c:url value='/login'/>">Login</a>
                    <a class="btn btn-secondary" href="<c:url value='/register'/>">Register</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <section class="detail-grid">
        <div class="panel section-panel" style="padding:22px;">
            <c:choose>
                <c:when test="${empty product.image}"><img class="detail-image" src="<c:url value='/assets/no-image.svg'/>" alt="No image"></c:when>
                <c:when test="${fn:startsWith(product.image, 'http://') or fn:startsWith(product.image, 'https://')}"><img class="detail-image" src="${product.image}" alt="${product.productName}"></c:when>
                <c:otherwise><img class="detail-image" src="<c:url value='/image?fname=${product.image}'/>" alt="${product.productName}"></c:otherwise>
            </c:choose>
        </div>
        <div class="panel detail-copy">
            <div class="eyebrow">${product.category.categoryname}</div>
            <h1 style="margin:12px 0 14px; font-size:42px;">${product.productName}</h1>
            <div class="price-line">Views: ${product.price.intValue()} on YouTube</div>
            <div class="meta-line">Track ${product.quantity} of 12 in the playlist</div>
            <p class="hero-lead" style="font-size:17px; max-width:none;">${product.description}</p>
            <div class="form-actions">
                <c:if test="${not empty youtubeUrl}">
                    <a class="btn btn-primary" href="${youtubeUrl}" target="_blank" rel="noopener noreferrer">Open on YouTube</a>
                </c:if>
                <a class="btn btn-secondary" href="<c:url value='/product'/>">Back to Catalog</a>
                <a class="btn btn-secondary" href="<c:url value='/home'/>">Back to Home</a>
            </div>
        </div>
    </section>
</div>
</body>
</html>
