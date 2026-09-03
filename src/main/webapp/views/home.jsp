<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Thang - Cai Thu Hai Catalog</title>
    <link rel="stylesheet" href="<c:url value='/assets/app-theme.css'/>">
</head>
<body class="theme-music">
<div class="theme-shell">
    <div class="theme-nav">
        <div class="theme-brand">
            Thang - Cai Thu Hai Catalog
            <small>OTP authentication, multipart upload, category-product relation, and public catalog pages</small>
        </div>
        <div class="theme-nav-links">
            <a class="btn btn-secondary" href="<c:url value='/home'/>">Home</a>
            <a class="btn btn-secondary" href="<c:url value='/product'/>">Catalog</a>
            <c:if test="${sessionScope.currentUser != null and sessionScope.currentUser.roleName == 'ADMIN'}">
                <a class="btn btn-secondary" href="<c:url value='/admin/products'/>">Admin</a>
            </c:if>
            <c:choose>
                <c:when test="${sessionScope.currentUser != null}">
                    <a class="btn btn-secondary" href="<c:url value='/profile'/>">Profile</a>
                    <span class="inline-note">Hello, ${sessionScope.currentUser.fullName}</span>
                    <a class="btn btn-primary" href="<c:url value='/logout'/>">Logout</a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-secondary" href="<c:url value='/login'/>">Login</a>
                    <a class="btn btn-primary" href="<c:url value='/register'/>">Register</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <c:if test="${not empty param.message}">
        <div class="message-box">${param.message}</div>
    </c:if>

    <section class="hero-grid">
        <div class="panel hero-copy">
            <div class="eyebrow">Featured Playlist</div>
            <h1 class="hero-title">Thang's Cai Thu Hai, reimagined as the demo catalog</h1>
            <p class="hero-lead">The assignment logic still uses category-product CRUD, OTP account activation, login, logout, forgot password with OTP, multipart upload, a homepage latest list, pagination on <strong>/product</strong>, and a detail page. The visible sample data now follows the requested track list from <strong>Cai Thu Hai</strong>.</p>
            <div class="cta-row">
                <a class="btn btn-primary" href="<c:url value='/product'/>">Open the Catalog</a>
                <c:if test="${sessionScope.currentUser != null and sessionScope.currentUser.roleName == 'ADMIN'}">
                    <a class="btn btn-secondary" href="<c:url value='/admin/product/add'/>">Create Entry</a>
                </c:if>
            </div>
        </div>
        <div class="panel stats-card">
            <div class="badge">Homepage Rule</div>
            <div>
                <div class="big-number">10</div>
                <div class="inline-note">The homepage still loads the 10 latest catalog entries to match the assignment brief.</div>
            </div>
            <div class="soft-panel" style="padding:22px;">
                <div>Authentication</div>
                <div class="big-number">OTP</div>
                <div class="inline-note">Registration activation and password reset are both wired through OTP verification pages.</div>
            </div>
        </div>
    </section>

    <section class="section-panel panel">
        <div class="section-head">
            <div>
                <h2>10 latest track entries</h2>
                <p>The records below come from the database and are ordered by newest first.</p>
            </div>
            <a class="btn btn-primary" href="<c:url value='/product'/>">View the full catalog</a>
        </div>

        <c:choose>
            <c:when test="${empty products}">
                <div class="empty-box">There are no track entries in the system yet.</div>
            </c:when>
            <c:otherwise>
                <div class="card-grid">
                    <c:forEach items="${products}" var="product">
                        <div class="item-card">
                            <c:choose>
                                <c:when test="${empty product.image}">
                                    <img src="<c:url value='/assets/no-image.svg'/>" alt="No image">
                                </c:when>
                                <c:when test="${fn:startsWith(product.image, 'http://') or fn:startsWith(product.image, 'https://')}">
                                    <img src="${product.image}" alt="${product.productName}">
                                </c:when>
                                <c:otherwise>
                                    <img src="<c:url value='/image?fname=${product.image}'/>" alt="${product.productName}">
                                </c:otherwise>
                            </c:choose>
                            <div class="meta-line">Category: ${product.category.categoryname} | Track ${product.quantity} of 12</div>
                            <h3>${product.productName}</h3>
                            <p>${product.description}</p>
                            <div class="price-line">Views: ${product.price.intValue()} on YouTube</div>
                            <div class="action-row">
                                <a class="btn btn-primary" href="<c:url value='/product/detail?id=${product.productId}'/>">View Detail</a>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</div>
</body>
</html>
