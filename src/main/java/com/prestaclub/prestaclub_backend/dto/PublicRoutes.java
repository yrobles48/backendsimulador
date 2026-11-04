package com.prestaclub.prestaclub_backend.dto;

import java.util.*;

public class PublicRoutes {
    public static final List<String> PUBLIC_URLS = Collections.unmodifiableList(Arrays.asList(
        "/generate-token",
        "/forgot-password",
        "/restablecer-contra",
        "/validate-password-token",
        "/health",
        "/prueba-s3",
        "/ws",
        "/ws/**",
        "/ws/info",
        "nuevo/tipo-cambio/**",
        "/ws/info/**",
        "/webhook/**",
            "/sms/**"
            //      "/nuevo/mensaje-plantilla/**"
    ));
}
