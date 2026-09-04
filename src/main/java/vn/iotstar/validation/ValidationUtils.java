package vn.iotstar.validation;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.regex.Pattern;

public final class ValidationUtils {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9._-]{3,50}$");
    private static final Pattern OTP_PATTERN = Pattern.compile("^\\d{6}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+()\\-\\s]{8,20}$");

    private ValidationUtils() {
    }

    public static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static String emptyIfNull(String value) {
        return value == null ? "" : value;
    }

    public static String normalizeEmail(String value) {
        String normalized = trimToNull(value);
        return normalized == null ? null : normalized.toLowerCase(Locale.ROOT);
    }

    public static boolean isValidEmail(String value) {
        String normalized = trimToNull(value);
        return normalized != null && EMAIL_PATTERN.matcher(normalized).matches();
    }

    public static boolean isValidUsername(String value) {
        String normalized = trimToNull(value);
        return normalized != null && USERNAME_PATTERN.matcher(normalized).matches();
    }

    public static boolean isValidOtp(String value) {
        String normalized = trimToNull(value);
        return normalized != null && OTP_PATTERN.matcher(normalized).matches();
    }

    public static boolean isValidPhone(String value) {
        String normalized = trimToNull(value);
        return normalized == null || PHONE_PATTERN.matcher(normalized).matches();
    }

    public static boolean isStatusValue(String value) {
        return "0".equals(trimToNull(value)) || "1".equals(trimToNull(value));
    }

    public static boolean exceedsLength(String value, int maxLength) {
        String normalized = trimToNull(value);
        return normalized != null && normalized.length() > maxLength;
    }

    public static boolean isPositiveInteger(String value) {
        try {
            return Integer.parseInt(value) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNonNegativeInteger(String value) {
        try {
            return Integer.parseInt(value) >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNonNegativeDecimal(String value) {
        try {
            return new BigDecimal(value).compareTo(BigDecimal.ZERO) >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidHttpUrl(String value) {
        String normalized = trimToNull(value);
        if (normalized == null) {
            return false;
        }
        try {
            URI uri = new URI(normalized);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            return host != null && ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme));
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
