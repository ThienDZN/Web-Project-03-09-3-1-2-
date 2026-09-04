package vn.iotstar.controller.admin;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.Category;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.util.LocalImageStorage;
import vn.iotstar.validation.ValidationErrors;
import vn.iotstar.validation.ValidationUtils;

@MultipartConfig
@WebServlet(urlPatterns = {
        "/admin/categories",
        "/admin/category/add",
        "/admin/category/insert",
        "/admin/category/edit",
        "/admin/category/update",
        "/admin/category/delete"
})
public class CategoryController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ICategoryService categoryService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String url = req.getServletPath();

        if (url.contains("/admin/categories")) {
            showList(req, resp);
            return;
        }
        if (url.contains("/admin/category/add")) {
            req.setAttribute("cate", new Category());
            req.getRequestDispatcher("/views/admin/category-add.jsp").forward(req, resp);
            return;
        }
        if (url.contains("/admin/category/edit")) {
            showEditForm(req, resp);
            return;
        }
        if (url.contains("/admin/category/delete")) {
            deleteCategory(req, resp);
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String url = req.getServletPath();
        if (url.contains("/admin/category/insert")) {
            insertCategory(req, resp);
            return;
        }
        if (url.contains("/admin/category/update")) {
            updateCategory(req, resp);
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ValidationErrors errors = new ValidationErrors();
        String keyword = ValidationUtils.trimToNull(req.getParameter("keyword"));
        if (ValidationUtils.exceedsLength(keyword, 50)) {
            errors.add("keyword", "Search keyword must not exceed 50 characters.");
        }

        List<Category> list = errors.hasErrors()
                ? categoryService.findAll()
                : (keyword == null ? categoryService.findAll() : categoryService.searchByName(keyword));
        req.setAttribute("errors", errors.asMap());
        req.setAttribute("listcate", list);
        req.setAttribute("keyword", keyword == null ? "" : keyword);
        req.getRequestDispatcher("/views/admin/category-list.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int id = parseId(req.getParameter("id"));
        Category category = categoryService.findById(id);
        if (category == null) {
            redirectWithMessage(resp, req.getContextPath() + "/admin/categories",
                    "Category entry not found.");
            return;
        }
        req.setAttribute("cate", category);
        req.getRequestDispatcher("/views/admin/category-edit.jsp").forward(req, resp);
    }

    private void insertCategory(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Category category = new Category();
        Map<String, String> formData = new LinkedHashMap<>();
        ValidationErrors errors = validateCategoryRequest(req, category, formData, null, null);
        bindFormState(req, category, formData, errors);
        if (errors.hasErrors()) {
            req.getRequestDispatcher("/views/admin/category-add.jsp").forward(req, resp);
            return;
        }

        try {
            category.setImages(resolveImage(req, null, "category"));
        } catch (IllegalArgumentException e) {
            errors.add("images1", e.getMessage());
            req.setAttribute("errors", errors.asMap());
            req.getRequestDispatcher("/views/admin/category-add.jsp").forward(req, resp);
            return;
        }

        try {
            categoryService.insert(category);
            redirectWithMessage(resp, req.getContextPath() + "/admin/categories",
                    "Category created successfully.");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/views/admin/category-add.jsp").forward(req, resp);
        }
    }

    private void updateCategory(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int categoryid = parseId(req.getParameter("categoryid"));
        Category category = categoryService.findById(categoryid);
        if (category == null) {
            redirectWithMessage(resp, req.getContextPath() + "/admin/categories",
                    "Category does not exist.");
            return;
        }

        String previousImage = category.getImages();
        Map<String, String> formData = new LinkedHashMap<>();
        ValidationErrors errors = validateCategoryRequest(req, category, formData, categoryid, previousImage);
        bindFormState(req, category, formData, errors);
        if (errors.hasErrors()) {
            req.getRequestDispatcher("/views/admin/category-edit.jsp").forward(req, resp);
            return;
        }

        try {
            category.setImages(resolveImage(req, previousImage, "category"));
        } catch (IllegalArgumentException e) {
            errors.add("images1", e.getMessage());
            req.setAttribute("errors", errors.asMap());
            req.getRequestDispatcher("/views/admin/category-edit.jsp").forward(req, resp);
            return;
        }

        try {
            categoryService.update(category);
            redirectWithMessage(resp, req.getContextPath() + "/admin/categories",
                    "Category updated successfully.");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/views/admin/category-edit.jsp").forward(req, resp);
        }
    }

    private void deleteCategory(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = parseId(req.getParameter("id"));
        try {
            Category category = categoryService.findById(id);
            if (category != null && isLocalImage(category.getImages())) {
                LocalImageStorage.deleteIfExists(category.getImages());
            }
            categoryService.delete(id);
            redirectWithMessage(resp, req.getContextPath() + "/admin/categories",
                    "Category deleted successfully.");
        } catch (Exception e) {
            redirectWithMessage(resp, req.getContextPath() + "/admin/categories", e.getMessage());
        }
    }

    private ValidationErrors validateCategoryRequest(HttpServletRequest req, Category category,
                                                     Map<String, String> formData, Integer currentId,
                                                     String previousImage) {
        ValidationErrors errors = new ValidationErrors();

        String categoryName = ValidationUtils.trimToNull(req.getParameter("categoryname"));
        formData.put("categoryname", ValidationUtils.emptyIfNull(categoryName));
        if (categoryName == null) {
            errors.add("categoryname", "Please enter the category name.");
        } else if (ValidationUtils.exceedsLength(categoryName, 50)) {
            errors.add("categoryname", "Category name must not exceed 50 characters.");
        } else {
            category.setCategoryname(categoryName);
            Category duplicate = categoryService.findByCategoryname(categoryName);
            if (duplicate != null && (currentId == null || duplicate.getCategoryid() != currentId)) {
                errors.add("categoryname", "The category name already exists.");
            }
        }

        String imageValue = ValidationUtils.trimToNull(req.getParameter("images"));
        formData.put("images", ValidationUtils.emptyIfNull(imageValue));
        if (ValidationUtils.exceedsLength(imageValue, 500)) {
            errors.add("images", "Image URL must not exceed 500 characters.");
        } else if (imageValue != null && !isAcceptedImageReference(imageValue, previousImage)) {
            errors.add("images", "Image URL must start with http:// or https://.");
        }
        category.setImages(imageValue == null ? ValidationUtils.trimToNull(previousImage) : imageValue);

        String statusValue = ValidationUtils.trimToNull(req.getParameter("status"));
        formData.put("status", ValidationUtils.emptyIfNull(statusValue));
        if (!ValidationUtils.isStatusValue(statusValue)) {
            errors.add("status", "Please select a valid status.");
        } else {
            category.setStatus("1".equals(statusValue) ? 1 : 0);
        }
        return errors;
    }

    private void bindFormState(HttpServletRequest req, Category category,
                               Map<String, String> formData, ValidationErrors errors) {
        req.setAttribute("cate", category);
        req.setAttribute("formData", formData);
        req.setAttribute("errors", errors.asMap());
    }

    private String resolveImage(HttpServletRequest req, String oldImage, String prefix)
            throws IOException, ServletException {
        String imageLink = ValidationUtils.trimToNull(req.getParameter("images"));
        Part part = req.getPart("images1");
        if (LocalImageStorage.hasUpload(part)) {
            String savedName = LocalImageStorage.storeImage(part, prefix);
            if (isLocalImage(oldImage)) {
                LocalImageStorage.deleteIfExists(oldImage);
            }
            return savedName;
        }
        if (imageLink != null) {
            return imageLink;
        }
        return ValidationUtils.trimToNull(oldImage);
    }

    private void redirectWithMessage(HttpServletResponse resp, String baseUrl, String message)
            throws IOException {
        String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
        resp.sendRedirect(baseUrl + "?message=" + encoded);
    }

    private int parseId(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The provided id is not valid.");
        }
    }

    private boolean isAcceptedImageReference(String value, String previousImage) {
        return ValidationUtils.isValidHttpUrl(value)
                || (isLocalImage(previousImage) && value.equals(ValidationUtils.trimToNull(previousImage)));
    }

    private boolean isLocalImage(String value) {
        return value != null
                && !value.isBlank()
                && !value.startsWith("http://")
                && !value.startsWith("https://");
    }
}
