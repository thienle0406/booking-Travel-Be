//package com.mytour.booking.service;
//
//import io.jsonwebtoken.*;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
//@Component
//public class JwtTokenProvider {
//
//    // Tạm thời hardcode secret và expiration
//    private final String jwtSecret = "YourSecureSecretKey1234567890";
//    private final long jwtExpirationInMs = 604800000L; // 7 ngày
//
//    // Tạo Token từ thông tin xác thực
//    public String generateToken(Authentication authentication) {
//        String username = authentication.getName();
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
//
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(expiryDate)
//                .signWith(SignatureAlgorithm.HS512, jwtSecret)
//                .compact();
//    }
//
//    // Lấy thông tin người dùng từ Token
//    public String getUsernameFromJWT(String token) {
//        Claims claims = Jwts.parser()
//                .setSigningKey(jwtSecret)
//                .parseClaimsJws(token)
//                .getBody();
//
//        return claims.getSubject();
//    }
//
//    // Xác thực Token
//    public boolean validateToken(String authToken) {
//        try {
//            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
//            return true;
//        } catch (MalformedJwtException ex) {
//            System.err.println("Invalid JWT token");
//        } catch (ExpiredJwtException ex) {
//            System.err.println("Expired JWT token");
//        } catch (UnsupportedJwtException ex) {
//            System.err.println("Unsupported JWT token");
//        } catch (IllegalArgumentException ex) {
//            System.err.println("JWT claims string is empty.");
//        }
//        return false;
//    }
//}