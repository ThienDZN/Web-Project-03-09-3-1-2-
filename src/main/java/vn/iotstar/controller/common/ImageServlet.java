package vn.iotstar.controller.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.util.LocalImageStorage;

@WebServlet("/image")
public class ImageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String fname = req.getParameter("fname");
        if (fname == null || fname.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/assets/no-image.svg");
            return;
        }

        Path filePath;
        try {
            filePath = LocalImageStorage.resolvePath(fname);
        } catch (IllegalArgumentException e) {
            resp.sendRedirect(req.getContextPath() + "/assets/no-image.svg");
            return;
        }

        if (!Files.isRegularFile(filePath)) {
            resp.sendRedirect(req.getContextPath() + "/assets/no-image.svg");
            return;
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        resp.setContentType(contentType);
        resp.setContentLengthLong(Files.size(filePath));
        Files.copy(filePath, resp.getOutputStream());
    }
}
