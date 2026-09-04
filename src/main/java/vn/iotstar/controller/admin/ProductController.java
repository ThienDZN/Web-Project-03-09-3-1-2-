package vn.iotstar.controller.admin;

import java.io.IOException;
import java.math.BigDecimal;
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
import vn.iotstar.entity.Product;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.service.impl.ProductServiceImpl;
import vn.iotstar.util.LocalImageStorage;
import vn.iotstar.validation.ValidationErrors;
import vn.iotstar.validation.ValidationUtils;

@MultipartConfig
@WebServlet(urlPatterns = {
        "/admin/products",
        "/admin/product/add",
        "/admin/product/insert",
        "/admin/product/edit",
        "/admin/product/update",
        "/admin/product/delete"
})
public class ProductController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final IProductService productService = new ProductServiceImpl();
    private final ICategoryService categoryService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        if ("/admin/products".equals(servletPath)) {
            showList(req, resp);
            return;
        }
        if ("/admin/product/add".equals(servletPath)) {
            showForm(req, resp, new Product(), "Create Catalog Entry", req.getContextPath() + "/admin/product/insert");
            return;
        }
        if ("/admin/product/edit".equals(servletPath)) {
            showEdit(req, resp);
            return;
        }
        if ("/admin/product/delete".equals(servletPath)) {
            deleteProduct(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        if ("/admin/product/insert".equals(servletPath)) {
            insertProduct(req, resp);
            return;
        }
        if ("/admin/product/update".equals(servletPath)) {
            updateProduct(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("products", productService.findAll());
        req.getRequestDispatcher("/views/admin/product-list.jsp").forward(req, resp);
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Product product = productService.findById(parseId(req.getParameter("id")));
        if (product == null) {
            redirect(resp, req.getContextPath() + "/admin/products", "Track entry not found.");
            return;
        }
        showForm(req, resp, product, "Update Catalog Entry", req.getContextPath() + "/admin/product/update");
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Product product, String formTitle, String formAction)
            throws ServletException, IOException {
        List<Category> categories = categoryService.findAll();
        req.setAttribute("product", product);
        req.setAttribute("categories", categories);
        req.setAttribute("formTitle", formTitle);
        req.setAttribute("formAction", formAction);
        req.getRequestDispatcher("/views/admin/product-form.jsp").forward(req, resp);
    }

    private void insertProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Product product = new Product();
        Map<String, String> formData = new LinkedHashMap<>();
        ValidationErrors errors = validateProductRequest(req, product, formData, null);
        bindFormState(req, product, formData, errors);
        if (errors.hasErrors()) {
            showForm(req, resp, product, "Create Catalog Entry", req.getContextPath() + "/admin/product/insert");
            return;
        }

        try {
            product.setImage(resolveImage(req, null, "product"));
        } catch (IllegalArgumentException e) {
            errors.add("imageFile", e.getMessage());
            req.setAttribute("errors", errors.asMap());
            showForm(req, resp, product, "Create Catalog Entry", req.getContextPath() + "/admin/product/insert");
            return;
        }

        try {
            productService.insert(product);
            redirect(resp, req.getContextPath() + "/admin/products", "Catalog entry created successfully.");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            showForm(req, resp, product, "Create Catalog Entry", req.getContextPath() + "/admin/product/insert");
        }
    }

    private void updateProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = parseId(req.getParameter("productId"));
        Product existing = productService.findById(id);
        if (existing == null) {
            redirect(resp, req.getContextPath() + "/admin/products", "Track entry not found.");
            return;
        }

        String previousImage = existing.getImage();
        Map<String, String> formData = new LinkedHashMap<>();
        ValidationErrors errors = validateProductRequest(req, existing, formData, previousImage);
        bindFormState(req, existing, formData, errors);
        if (errors.hasErrors()) {
            showForm(req, resp, existing, "Update Catalog Entry", req.getContextPath() + "/admin/product/update");
            return;
        }

        try {
            existing.setImage(resolveImage(req, previousImage, "product"));
        } catch (IllegalArgumentException e) {
            errors.add("imageFile", e.getMessage());
            req.setAttribute("errors", errors.asMap());
            showForm(req, resp, existing, "Update Catalog Entry", req.getContextPath() + "/admin/product/update");
            return;
        }

        try {
            productService.update(existing);
            redirect(resp, req.getContextPath() + "/admin/products", "Catalog entry updated successfully.");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            showForm(req, resp, existing, "Update Catalog Entry", req.getContextPath() + "/admin/product/update");
        }
    }

    private void deleteProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseId(req.getParameter("id"));
        Product product = productService.findById(id);
        try {
            if (product != null && isLocalImage(product.getImage())) {
                LocalImageStorage.deleteIfExists(product.getImage());
            }
            productService.delete(id);
            redirect(resp, req.getContextPath() + "/admin/products", "Catalog entry deleted successfully.");
        } catch (Exception e) {
            redirect(resp, req.getContextPath() + "/admin/products", e.getMessage());
        }
    }

    private ValidationErrors validateProductRequest(HttpServletRequest req, Product product,
                                                    Map<String, String> formData, String previousImage) {
        ValidationErrors errors = new ValidationErrors();

        String productName = ValidationUtils.trimToNull(req.getParameter("productName"));
        formData.put("productName", ValidationUtils.emptyIfNull(productName));
        if (productName == null) {
            errors.add("productName", "Please enter the title.");
        } else if (ValidationUtils.exceedsLength(productName, 150)) {
            errors.add("productName", "Title must not exceed 150 characters.");
        }
        product.setProductName(productName);

        String categoryIdValue = ValidationUtils.trimToNull(req.getParameter("categoryId"));
        formData.put("categoryId", ValidationUtils.emptyIfNull(categoryIdValue));
        if (!ValidationUtils.isPositiveInteger(categoryIdValue)) {
            errors.add("categoryId", "Please choose a valid category.");
        } else {
            Category category = categoryService.findById(Integer.parseInt(categoryIdValue));
            if (category == null) {
                errors.add("categoryId", "Selected category does not exist.");
            } else {
                product.setCategory(category);
            }
        }

        String priceValue = ValidationUtils.trimToNull(req.getParameter("price"));
        formData.put("price", ValidationUtils.emptyIfNull(priceValue));
        if (priceValue == null) {
            errors.add("price", "Please enter the price.");
        } else if (!ValidationUtils.isNonNegativeDecimal(priceValue)) {
            errors.add("price", "Price must be a number greater than or equal to 0.");
        } else {
            product.setPrice(new BigDecimal(priceValue));
        }

        String quantityValue = ValidationUtils.trimToNull(req.getParameter("quantity"));
        formData.put("quantity", ValidationUtils.emptyIfNull(quantityValue));
        if (quantityValue == null) {
            errors.add("quantity", "Please enter the quantity.");
        } else if (!ValidationUtils.isNonNegativeInteger(quantityValue)) {
            errors.add("quantity", "Quantity must be an integer greater than or equal to 0.");
        } else {
            product.setQuantity(Integer.parseInt(quantityValue));
        }

        String description = ValidationUtils.trimToNull(req.getParameter("description"));
        formData.put("description", ValidationUtils.emptyIfNull(description));
        if (ValidationUtils.exceedsLength(description, 2000)) {
            errors.add("description", "Description must not exceed 2000 characters.");
        }
        product.setDescription(description);

        String imageValue = ValidationUtils.trimToNull(req.getParameter("image"));
        formData.put("image", ValidationUtils.emptyIfNull(imageValue));
        if (ValidationUtils.exceedsLength(imageValue, 500)) {
            errors.add("image", "Image URL must not exceed 500 characters.");
        } else if (imageValue != null && !isAcceptedImageReference(imageValue, previousImage)) {
            errors.add("image", "Image URL must start with http:// or https://.");
        }
        product.setImage(imageValue == null ? ValidationUtils.trimToNull(previousImage) : imageValue);

        String statusValue = ValidationUtils.trimToNull(req.getParameter("status"));
        formData.put("status", ValidationUtils.emptyIfNull(statusValue));
        if (!ValidationUtils.isStatusValue(statusValue)) {
            errors.add("status", "Please select a valid status.");
        } else {
            product.setStatus("1".equals(statusValue) ? 1 : 0);
        }

        return errors;
    }

    private void bindFormState(HttpServletRequest req, Product product,
                               Map<String, String> formData, ValidationErrors errors) {
        req.setAttribute("product", product);
        req.setAttribute("formData", formData);
        req.setAttribute("errors", errors.asMap());
    }

    private String resolveImage(HttpServletRequest req, String oldImage, String prefix)
            throws IOException, ServletException {
        String imageLink = ValidationUtils.trimToNull(req.getParameter("image"));
        Part part = req.getPart("imageFile");
        if (LocalImageStorage.hasUpload(part)) {
            String savedName = LocalImageStorage.storeImage(part, prefix);
            if (isLocalImage(oldImage)) {
                LocalImageStorage.deleteIfExists(oldImage);
            }
            return savedName;
        }
        return imageLink != null ? imageLink : ValidationUtils.trimToNull(oldImage);
    }

    private void redirect(HttpServletResponse resp, String baseUrl, String message) throws IOException {
        resp.sendRedirect(baseUrl + "?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
    }

    private Long parseId(String raw) {
        try {
            return Long.parseLong(raw);
        } catch (Exception e) {
            return -1L;
        }
    }

    private boolean isAcceptedImageReference(String value, String previousImage) {
        return ValidationUtils.isValidHttpUrl(value)
                || (isLocalImage(previousImage) && value.equals(ValidationUtils.trimToNull(previousImage)));
    }

    private boolean isLocalImage(String value) {
        return value != null && !value.isBlank() && !value.startsWith("http://") && !value.startsWith("https://");
    }
}
