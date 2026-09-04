<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Category</title>
    <link rel="stylesheet" href="<c:url value='/assets/app-theme.css'/>">
</head>
<body class="theme-music">
<div class="theme-shell">
    <div class="panel form-shell">
        <div class="eyebrow">Admin Category</div>
        <h1>Update Category</h1>
        <c:if test="${not empty error}"><div class="error-box">${error}</div></c:if>
        <form action="<c:url value='/admin/category/update'/>" method="post" enctype="multipart/form-data">
            <input type="hidden" name="categoryid" value="<c:out value='${cate.categoryid}'/>">
            <div class="form-group">
                <label>Category Name</label>
                <input class="form-input${not empty errors.categoryname ? ' is-invalid' : ''}" type="text" name="categoryname"
                       value="<c:out value='${not empty formData.categoryname ? formData.categoryname : cate.categoryname}'/>" placeholder="Enter category name">
                <c:if test="${not empty errors.categoryname}">
                    <div class="invalid-feedback d-block">${errors.categoryname}</div>
                </c:if>
            </div>
            <div class="form-group">
                <label>Image URL</label>
                <input class="form-input${not empty errors.images ? ' is-invalid' : ''}" type="text" name="images"
                       value="<c:out value='${not empty formData.images ? formData.images : cate.images}'/>" placeholder="https://...">
                <c:if test="${not empty errors.images}">
                    <div class="invalid-feedback d-block">${errors.images}</div>
                </c:if>
            </div>
            <c:choose>
                <c:when test="${empty cate.images}"><img class="preview-image" src="<c:url value='/assets/no-image.svg'/>" alt="No image"></c:when>
                <c:when test="${fn:startsWith(cate.images, 'http://') or fn:startsWith(cate.images, 'https://')}"><img class="preview-image" src="${cate.images}" alt="${cate.categoryname}"></c:when>
                <c:otherwise><img class="preview-image" src="<c:url value='/image?fname=${cate.images}'/>" alt="${cate.categoryname}"></c:otherwise>
            </c:choose>
            <div class="form-group">
                <label>Upload New Image</label>
                <input class="form-file${not empty errors.images1 ? ' is-invalid' : ''}" type="file" name="images1" accept="image/*">
                <c:if test="${not empty errors.images1}">
                    <div class="invalid-feedback d-block">${errors.images1}</div>
                </c:if>
            </div>
            <div class="form-group">
                <label>Status</label>
                <div class="radio-row">
                    <label><input type="radio" name="status" value="1" ${not empty formData.status ? (formData.status == '1' ? 'checked' : '') : (cate.status == 1 ? 'checked' : '')}> Visible</label>
                    <label><input type="radio" name="status" value="0" ${not empty formData.status ? (formData.status == '0' ? 'checked' : '') : (cate.status != 1 ? 'checked' : '')}> Locked</label>
                </div>
                <c:if test="${not empty errors.status}">
                    <div class="invalid-feedback d-block">${errors.status}</div>
                </c:if>
            </div>
            <div class="form-actions">
                <button class="btn btn-primary" type="submit">Update Category</button>
                <a class="btn btn-secondary" href="<c:url value='/admin/categories'/>">Back to Categories</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>
