package backend.datn.config;

import backend.datn.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 1. Kiểm tra header: Nếu không có header Authorization hoặc không bắt đầu bằng "Bearer " -> Bỏ qua filter này
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Lấy JWT token (cắt bỏ 7 ký tự đầu tiên là "Bearer ")
        jwt = authHeader.substring(7);

        // 3. Trích xuất email (username) từ chuỗi token thông qua JwtService
        userEmail = jwtService.extractUsername(jwt);

        // 4. Nếu lấy được email và hiện tại SecurityContext chưa có thông tin đăng nhập
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Tìm user trong database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 5. Xác thực token có hợp lệ và chưa hết hạn không
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 6. Tạo đối tượng Authentication và set vào SecurityContext
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Đánh dấu là user đã đăng nhập thành công cho request này
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 7. Chuyển request đi tiếp tới các filter khác hoặc Controller
        filterChain.doFilter(request, response);
    }
}
