<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Catalog</title>
    <link rel="stylesheet" href="<c:url value='/assets/app-theme.css'/>">
</head>
<body class="theme-music">
<div class="theme-shell">
    <div class="theme-nav">
        <div class="theme-brand">
            Catalog Management
            <small>CRUD entries with multipart upload, still backed by the product table required in the assignment</small>
        </div>
        <div class="theme-nav-links">
            <a class="btn btn-secondary" href="<c:url value='/home'/>">Home</a>
            <a class="btn btn-secondary" href="<c:url value='/admin/categories'/>">Categories</a>
            <a class="btn btn-secondary" href="<c:url value='/profile'/>">Profile</a>
            <a class="btn btn-primary" href="<c:url value='/admin/product/add'/>">Create Entry</a>
            <a class="btn btn-secondary" href="<c:url value='/logout'/>">Logout</a>
        </div>
    </div>

    <c:if test="${not empty param.message}"><div class="message-box">${param.message}</div></c:if>

    <section class="panel section-panel">
        <div class="section-head">
            <div>
                <h1>Catalog table</h1>
                <p>Total entries: ${fn:length(products)}</p>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty products}">
                <div class="empty-box">There are no entries in the catalog yet.</div>
            </c:when>
            <c:otherwise>
                <table class="data-table">
                    <tr>
                        <th>ID</th>
                        <th>Image</th>
                        <th>Title</th>
                        <th>Category</th>
                        <th>Price</th>
                        <th>Qty</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    <c:forEach items="${products}" var="product">
                        <tr>
                            <td>${product.productId}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${empty product.image}"><img class="table-thumb" src="<c:url value='/assets/no-image.svg'/>" alt="No image"></c:when>
                                    <c:when test="${fn:startsWith(product.image, 'http://') or fn:startsWith(product.image, 'https://')}"><img class="table-thumb" src="${product.image}" alt="${product.productName}"></c:when>
                                    <c:otherwise><img class="table-thumb" src="<c:url value='/image?fname=${product.image}'/>" alt="${product.productName}"></c:otherwise>
                                </c:choose>
                            </td>
                            <td>${product.productName}</td>
                            <td>${product.category.categoryname}</td>
                            <td><fmt:formatNumber value="${product.price}" type="number" groupingUsed="true"/> VND</td>
                            <td>${product.quantity}</td>
                            <td><c:choose><c:when test="${product.status == 1}">Visible</c:when><c:otherwise>Hidden</c:otherwise></c:choose></td>
                            <td>
                                <div class="action-row">
                                    <a class="btn btn-secondary" href="<c:url value='/admin/product/edit?id=${product.productId}'/>">Edit</a>
                                    <a class="btn btn-danger" href="<c:url value='/admin/product/delete?id=${product.productId}'/>">Delete</a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </section>
</div>
</body>
</html>
