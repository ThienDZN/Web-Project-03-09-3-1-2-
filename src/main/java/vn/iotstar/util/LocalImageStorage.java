package vn.iotstar.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import jakarta.servlet.http.Part;
import vn.iotstar.config.UploadConstants;

public final class LocalImageStorage {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp");

    private LocalImageStorage() {
    }

    public static boolean hasUpload(Part part) {
        return part != null && part.getSize() > 0;
    }

    public static String storeImage(Part part, String prefix) throws IOException {
        if (!hasUpload(part)) {
            throw new IllegalArgumentException("Please choose an image file to upload.");
        }

        String extension = extractExtension(part.getSubmittedFileName());
        validateContentType(part.getContentType());

        Path uploadRoot = ensureUploadRoot();
        String safePrefix = sanitizePrefix(prefix);
        String savedName = safePrefix + "-" + System.currentTimeMillis() + "-"
                + UUID.randomUUID().toString().replace("-", "") + extension;
        Path target = uploadRoot.resolve(savedName).normalize();

        try (InputStream inputStream = part.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return savedName;
    }

    public static void deleteIfExists(String fileName) throws IOException {
        if (!isLocalFile(fileName)) {
            return;
        }
        Files.deleteIfExists(resolvePath(fileName));
    }

    public static Path resolvePath(String fileName) {
        String normalizedName = normalizeStoredName(fileName);
        Path uploadRoot = Paths.get(UploadConstants.DIR).toAbsolutePath().normalize();
        Path resolved = uploadRoot.resolve(normalizedName).normalize();
        if (!resolved.startsWith(uploadRoot)) {
            throw new IllegalArgumentException("Invalid image path.");
        }
        return resolved;
    }

    public static boolean isLocalFile(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String trimmed = value.trim();
        return !trimmed.startsWith("http://") && !trimmed.startsWith("https://");
    }

    private static Path ensureUploadRoot() throws IOException {
        Path uploadRoot = Paths.get(UploadConstants.DIR).toAbsolutePath().normalize();
        Files.createDirectories(uploadRoot);
        return uploadRoot;
    }

    private static void validateContentType(String contentType) {
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("Only image files can be uploaded.");
        }
    }

    private static String extractExtension(String submittedFileName) {
        String cleanName = baseName(submittedFileName);
        int dotIndex = cleanName.lastIndexOf('.');
        if (dotIndex < 0) {
            throw new IllegalArgumentException("The uploaded file must have an image extension.");
        }

        String extension = cleanName.substring(dotIndex).toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Only JPG, JPEG, PNG, GIF, WEBP, or BMP files are allowed.");
        }
        return extension;
    }

    private static String normalizeStoredName(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("Image name must not be empty.");
        }
        String trimmed = fileName.trim();
        if (trimmed.isEmpty() || trimmed.contains("/") || trimmed.contains("\\") || trimmed.contains("..")) {
            throw new IllegalArgumentException("Image name is not valid.");
        }
        return trimmed;
    }

    private static String sanitizePrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return "image";
        }
        return prefix.trim().replaceAll("[^a-zA-Z0-9-]+", "-").toLowerCase(Locale.ROOT);
    }

    private static String baseName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("The uploaded file does not have a valid name.");
        }
        String normalized = value.replace('\\', '/');
        int index = normalized.lastIndexOf('/');
        return index >= 0 ? normalized.substring(index + 1) : normalized;
    }
}
