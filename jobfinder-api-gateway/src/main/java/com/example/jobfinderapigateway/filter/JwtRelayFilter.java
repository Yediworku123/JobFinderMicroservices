package com.example.jobfinderapigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtRelayFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .cast(JwtAuthenticationToken.class)
                .map(auth -> auth.getToken())
                .flatMap(jwt -> {

                    String userId = jwt.getSubject();
                    String email = jwt.getClaimAsString("email");

                    // ⬇️ EXTRACT ROLES HERE
                    Map<String, Object> realmAccess = jwt.getClaim("realm_access");
                    List<String> roles = List.of(); // Default empty

                    if (realmAccess != null && realmAccess.get("roles") instanceof List<?> roleList) {
                        roles = roleList.stream()
                                .map(Object::toString)
                                .collect(Collectors.toList());
                    }

                    // Join roles into a single string, e.g., "USER,ADMIN"
                    String rolesHeader = String.join(",", roles);

                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .header("X-User-Id", userId)
                            .header("X-User-Email", email)
                            .header("X-Roles", rolesHeader) // ⬇️ ADD THIS HEADER
                            .build();

                    return chain.filter(exchange.mutate()
                            .request(mutatedRequest)
                            .build());
                });
    }

    @Override
    public int getOrder() {
        return -1; // Run early
    }
}