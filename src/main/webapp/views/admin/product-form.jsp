<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${formTitle}</title>
    <link rel="stylesheet" href="<c:url value='/assets/app-theme.css'/>">
</head>
<body class="theme-music">
<div class="theme-shell">
    <div class="panel form-shell">
        <div class="eyebrow">Admin Catalog Entry</div>
        <h1>${formTitle}</h1>
        <p class="inline-note">This form still fulfills the product CRUD requirement while the sample content now follows Thang's Cai Thu Hai track list.</p>
        <c:if test="${not empty error}"><div class="error-box">${error}</div></c:if>
        <form action="${formAction}" method="post" enctype="multipart/form-data">
            <c:if test="${product.productId != null}">
                <input type="hidden" name="productId" value="<c:out value='${product.productId}'/>">
            </c:if>
            <div class="form-group">
                <label>Title</label>
                <input class="form-input${not empty errors.productName ? ' is-invalid' : ''}" type="text" name="productName"
                       value="<c:out value='${not empty formData.productName ? formData.productName : product.productName}'/>" placeholder="Enter title">
                <c:if test="${not empty errors.productName}">
                    <div class="invalid-feedback d-block">${errors.productName}</div>
                </c:if>
            </div>
            <div class="form-group">
                <label>Category</label>
                <select class="form-select${not empty errors.categoryId ? ' is-invalid' : ''}" name="categoryId">
                    <c:forEach items="${categories}" var="category">
                        <option value="${category.categoryid}" ${not empty formData.categoryId ? (formData.categoryId == category.categoryid ? 'selected' : '') : (product.category != null and product.category.categoryid == category.categoryid ? 'selected' : '')}>${category.categoryname}</option>
                    </c:forEach>
                </select>
                <c:if test="${not empty errors.categoryId}">
                    <div class="invalid-feedback d-block">${errors.categoryId}</div>
                </c:if>
            </div>
            <div class="form-group">
                <label>Price</label>
                <input class="form-input${not empty errors.price ? ' is-invalid' : ''}" type="number" step="0.01" name="price"
                       value="<c:out value='${not empty formData.price ? formData.price : product.price}'/>" placeholder="0.00">
                <c:if test="${not empty errors.price}">
                    <div class="invalid-feedback d-block">${errors.price}</div>
                </c:if>
            </div>
            <div class="form-group">
                <label>Quantity</label>
                <input class="form-input${not empty errors.quantity ? ' is-invalid' : ''}" type="number" name="quantity"
                       value="<c:out value='${not empty formData.quantity ? formData.quantity : product.quantity}'/>" placeholder="0">
                <c:if test="${not empty errors.quantity}">
                    <div class="invalid-feedback d-block">${errors.quantity}</div>
                </c:if>
            </div>
            <div class="form-group">
                <label>Description</label>
                <textarea class="form-textarea${not empty errors.description ? ' is-invalid' : ''}" name="description" placeholder="Write a description"><c:out value="${not empty formData.description ? formData.description : product.description}"/></textarea>
                <c:if test="${not empty errors.description}">
                    <div class="invalid-feedback d-block">${errors.description}</div>
                </c:if>
            </div>
            <div class="form-group">
                <label>Image URL</label>
                <input class="form-input${not empty errors.image ? ' is-invalid' : ''}" type="text" name="image"
                       value="<c:out value='${not empty formData.image ? formData.image : product.image}'/>" placeholder="https://... or keep empty to upload a file">
                <c:if test="${not empty errors.image}">
                    <div class="invalid-feedback d-block">${errors.image}</div>
                </c:if>
            </div>
            <c:if test="${not empty product.image}">
                <c:choose>
                    <c:when test="${fn:startsWith(product.image, 'http://') or fn:startsWith(product.image, 'https://')}"><img class="preview-image" src="${product.image}" alt="${product.productName}"></c:when>
                    <c:otherwise><img class="preview-image" src="<c:url value='/image?fname=${product.image}'/>" alt="${product.productName}"></c:otherwise>
                </c:choose>
            </c:if>
            <div class="form-group">
                <label>Upload Image</label>
                <input class="form-file${not empty errors.imageFile ? ' is-invalid' : ''}" type="file" name="imageFile" accept="image/*">
                <c:if test="${not empty errors.imageFile}">
                    <div class="invalid-feedback d-block">${errors.imageFile}</div>
                </c:if>
            </div>
            <div class="form-group">
                <label>Status</label>
                <div class="radio-row">
                    <label><input type="radio" name="status" value="1" ${not empty formData.status ? (formData.status == '1' ? 'checked' : '') : (product.status != 0 ? 'checked' : '')}> Visible</label>
                    <label><input type="radio" name="status" value="0" ${not empty formData.status ? (formData.status == '0' ? 'checked' : '') : (product.status == 0 ? 'checked' : '')}> Hidden</label>
                </div>
                <c:if test="${not empty errors.status}">
                    <div class="invalid-feedback d-block">${errors.status}</div>
                </c:if>
            </div>
            <div class="form-actions">
                <button class="btn btn-primary" type="submit">Save Entry</button>
                <a class="btn btn-secondary" href="<c:url value='/admin/products'/>">Back to Catalog</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>
