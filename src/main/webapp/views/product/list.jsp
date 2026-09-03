<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Track Catalog</title>
    <link rel="stylesheet" href="<c:url value='/assets/app-theme.css'/>">
</head>
<body class="theme-music">
<div class="theme-shell">
    <div class="theme-nav">
        <div class="theme-brand">
            Public Track Catalog
            <small>Paginated 6 items per page at the exact URL path /product</small>
        </div>
        <div class="theme-nav-links">
            <a class="btn btn-secondary" href="<c:url value='/home'/>">Home</a>
            <a class="btn btn-primary" href="<c:url value='/product'/>">Refresh</a>
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

    <c:if test="${not empty param.message}"><div class="message-box">${param.message}</div></c:if>

    <section class="panel section-panel">
        <div class="section-head">
            <div>
                <h1>All catalog entries</h1>
                <p>This page remains the public, paginated listing required by the assignment.</p>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty products}">
                <div class="empty-box">There are no entries available for display.</div>
            </c:when>
            <c:otherwise>
                <div class="card-grid">
                    <c:forEach items="${products}" var="product">
                        <div class="item-card">
                            <c:choose>
                                <c:when test="${empty product.image}"><img src="<c:url value='/assets/no-image.svg'/>" alt="No image"></c:when>
                                <c:when test="${fn:startsWith(product.image, 'http://') or fn:startsWith(product.image, 'https://')}"><img src="${product.image}" alt="${product.productName}"></c:when>
                                <c:otherwise><img src="<c:url value='/image?fname=${product.image}'/>" alt="${product.productName}"></c:otherwise>
                            </c:choose>
                            <div class="meta-line">Category: ${product.category.categoryname}</div>
                            <h3>${product.productName}</h3>
                            <p>${product.description}</p>
                            <div class="price-line">Views: ${product.price.intValue()} on YouTube</div>
                            <div class="meta-line">Track ${product.quantity} of 12</div>
                            <div class="action-row">
                                <a class="btn btn-primary" href="<c:url value='/product/detail?id=${product.productId}'/>">View Detail</a>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                <div class="pagination">
                    <c:forEach begin="1" end="${totalPages}" var="pageNumber">
                        <c:choose>
                            <c:when test="${pageNumber == currentPage}">
                                <span class="page-pill active">${pageNumber}</span>
                            </c:when>
                            <c:otherwise>
                                <a class="page-pill" href="<c:url value='/product?page=${pageNumber}'/>">${pageNumber}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</div>
</body>
</html>
