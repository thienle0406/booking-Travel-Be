package com.mytour.booking.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // Khóa bí mật dùng để ký (sign) JWT. Đọc từ application.properties
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    // Thời gian hiệu lực của token (tính bằng mili giây)
    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    private Key key;

    // Phương thức tạo Key an toàn từ chuỗi jwtSecret (được mã hóa Base64)
    private Key getSigningKey() {
        if (this.key == null) {
            // Giải mã chuỗi Base64 từ cấu hình để lấy byte array
            byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
            // Tạo Key HMAC SHA-256
            this.key = Keys.hmacShaKeyFor(keyBytes);
        }
        return this.key;
    }

    /**
     * Tạo JWT từ đối tượng Authentication (sử dụng khi đăng nhập thành công)
     */
    public String generateToken(Authentication authentication) {
        // Lấy thông tin user (thường là UserDetails)
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Date now = new Date();
        // Tính toán thời gian hết hạn
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // Xây dựng Token
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // Subject là tên người dùng
                .setIssuedAt(now)                      // Thời điểm tạo token
                .setExpiration(expiryDate)             // Thời điểm hết hạn
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Ký token
                .compact();
    }

    /**
     * Lấy username từ JWT (sử dụng khi xác thực token trong Filter)
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Kiểm tra tính hợp lệ của JWT
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException |
                 IllegalArgumentException | SignatureException ex) {
            // Log lỗi chi tiết ở đây (ví dụ: logger.error("JWT validation error: {}", ex.getMessage());)
            System.err.println("JWT validation error: " + ex.getMessage());
        }
        return false;
    }
}