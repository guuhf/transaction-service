package com.guuh.transaction_service.infrastructure.configs;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SecurityScheme(name = AuthorizationConfig.SECURITY_SCHEME, type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT", scheme = "bearer")
public class AuthorizationConfig {

    public static final String SECURITY_SCHEME = "bearerAuth";
}
