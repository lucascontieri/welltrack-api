package com.welltrack.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class SwaggerIpAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Autowired
    private SwaggerSecurityProperties swaggerSecurityProperties;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        var clientIp = ClientIpResolver.resolve(context.getRequest());
        var allowed = swaggerSecurityProperties.getAllowedIps().stream()
                .map(ClientIpResolver::normalize)
                .anyMatch(clientIp::equals);
        return new AuthorizationDecision(allowed);
    }
}
