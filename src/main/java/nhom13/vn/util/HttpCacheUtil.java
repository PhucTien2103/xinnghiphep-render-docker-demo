package nhom13.vn.util;

import jakarta.servlet.http.HttpServletResponse;

public final class HttpCacheUtil {

    private HttpCacheUtil() {
    }

    /** Avoids stale dashboard / statistics HTML when data changes server-side. */
    public static void disableCaching(HttpServletResponse resp) {
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, private");
        resp.setHeader("Pragma", "no-cache");
    }
}
