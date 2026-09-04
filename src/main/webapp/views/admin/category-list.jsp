<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Category List</title>
    <link rel="stylesheet" href="<c:url value='/assets/app-theme.css'/>">
</head>
<body class="theme-music">
<div class="theme-shell">
    <div class="theme-nav">
        <div class="theme-brand">
            Category Management
            <small>One-to-many relation support for the assignment catalog entries</small>
        </div>
        <div class="theme-nav-links">
            <a class="btn btn-secondary" href="<c:url value='/home'/>">Home</a>
            <a class="btn btn-secondary" href="<c:url value='/admin/products'/>">Catalog</a>
            <a class="btn btn-secondary" href="<c:url value='/profile'/>">Profile</a>
            <a class="btn btn-primary" href="<c:url value='/admin/category/add'/>">Add Category</a>
            <a class="btn btn-secondary" href="<c:url value='/logout'/>">Logout</a>
        </div>
    </div>

    <c:if test="${not empty param.message}"><div class="message-box">${param.message}</div></c:if>

    <section class="panel section-panel">
        <div class="section-head">
            <div>
                <h1>Category table</h1>
                <p>Total categories: ${fn:length(listcate)}</p>
            </div>
        </div>

        <form action="<c:url value='/admin/categories'/>" method="get" class="search-row">
            <input class="form-input${not empty errors.keyword ? ' is-invalid' : ''}" type="text" name="keyword" value="<c:out value='${keyword}'/>" placeholder="Search by category name">
            <button class="btn btn-primary" type="submit">Search</button>
            <a class="btn btn-secondary" href="<c:url value='/admin/categories'/>">Reset</a>
            <c:if test="${not empty errors.keyword}">
                <div class="invalid-feedback d-block">${errors.keyword}</div>
            </c:if>
        </form>

        <c:choose>
            <c:when test="${empty listcate}">
                <div class="empty-box">There are no categories yet.</div>
            </c:when>
            <c:otherwise>
                <table class="data-table">
                    <tr>
                        <th>No.</th>
                        <th>Image</th>
                        <th>Name</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                    <c:forEach items="${listcate}" var="cate" varStatus="stt">
                        <tr>
                            <td>${stt.index + 1}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${empty cate.images}"><img class="table-thumb" src="<c:url value='/assets/no-image.svg'/>" alt="No image"></c:when>
                                    <c:when test="${fn:startsWith(cate.images, 'http://') or fn:startsWith(cate.images, 'https://')}"><img class="table-thumb" src="${cate.images}" alt="${cate.categoryname}"></c:when>
                                    <c:otherwise><img class="table-thumb" src="<c:url value='/image?fname=${cate.images}'/>" alt="${cate.categoryname}"></c:otherwise>
                                </c:choose>
                            </td>
                            <td>${cate.categoryname}</td>
                            <td><c:choose><c:when test="${cate.status == 1}">Visible</c:when><c:otherwise>Locked</c:otherwise></c:choose></td>
                            <td>
                                <div class="action-row">
                                    <a class="btn btn-secondary" href="<c:url value='/admin/category/edit?id=${cate.categoryid}'/>">Edit</a>
                                    <a class="btn btn-danger" href="<c:url value='/admin/category/delete?id=${cate.categoryid}'/>">Delete</a>
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
