package hello.cokezet.temporary.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.cokezet.temporary.global.common.ApiResult;
import hello.cokezet.temporary.global.error.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException {

        try {
            String token = resolveToken(request);

            if (StringUtils.hasText(token)) {
                try {
                    if (jwtProvider.validateToken(token)) {
                        Authentication authentication = getAuthentication(token);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("JWT 토큰 인증 성공: {}", authentication.getName());
                    }
                } catch (ExpiredJwtException e) {
                    log.error("만료된 JWT 토큰", e);
                    sendErrorResponse(response, ErrorCode.EXPIRED_TOKEN);
                    return;
                } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
                    log.error("유효하지 않은 JWT 토큰", e);
                    sendErrorResponse(response, ErrorCode.INVALID_TOKEN);
                    return;
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("JWT 필터 처리 중 예외 발생", e);
            sendErrorResponse(response, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResult<Void> apiResult = ApiResult.error(errorCode.getCode(), errorCode.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(apiResult));
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    private Authentication getAuthentication(String token) {
        Claims claims = jwtProvider.getClaimsFromToken(token);
        Long userId = Long.parseLong(claims.getSubject());
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);

        UserPrincipal principal = new UserPrincipal(userId, email);

        return new UsernamePasswordAuthenticationToken(
                principal,
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
    }
}
