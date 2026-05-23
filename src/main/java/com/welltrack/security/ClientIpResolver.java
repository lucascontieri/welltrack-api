package com.welltrack.security;

import jakarta.servlet.http.HttpServletRequest;

final class ClientIpResolver {

    private ClientIpResolver() {
    }

    static String resolve(HttpServletRequest request) {
        return normalize(request.getRemoteAddr());
    }

    static String normalize(String ip) {
        if (ip == null || ip.isBlank()) {
            return "";
        }
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        if (ip.startsWith("::ffff:")) {
            return ip.substring("::ffff:".length());
        }
        return ip.trim();
    }
}
