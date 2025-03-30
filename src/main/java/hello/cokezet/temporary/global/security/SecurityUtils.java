package hello.cokezet.temporary.global.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    /**
     * 현재 인증된 사용자가 회원(USER)인지 확인
     * @return 회원이면 true, 아니면 false
     */
    public static boolean isUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }

    /**
     * 현재 인증된 사용자가 게스트(GUEST)인지 확인
     * @return 게스트면 true, 아니면 false
     */
    public static boolean isGuest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_GUEST"));
    }
}
