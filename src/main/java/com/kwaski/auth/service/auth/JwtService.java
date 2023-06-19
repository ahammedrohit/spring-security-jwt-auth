//package com.kwaski.auth.service;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.JWTVerifier;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.auth0.jwt.exceptions.JWTDecodeException;
//import com.auth0.jwt.interfaces.Claim;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import com.kwaski.auth.entity.TokenEntity;
//import com.kwaski.auth.entity.UserEntity;
//import lombok.Builder;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//@Service
//@RequiredArgsConstructor
//public class JwtService {
//
//    @Value("${jwt.accessToken.signInKey}")
//    private String SECRET_KEY;
//
//    private final TokenService tokenService;
//
//
//    public String extractUsername(String token) {
//        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).build();
//        DecodedJWT decodedJWT = verifier.verify(token);
//        return decodedJWT.getClaim("name").asString();
//    }
//
//    public String generateToken(UserEntity userEntity) {
//
//        String accessToken = tokenService.findAccessTokenByUserName(userEntity.getUsername());
//
////        if (accessToken != null) {
////            return accessToken;
////        } else {
////            return generateToken(new HashMap<>(), userEntity);
////        }
//
//        return generateToken(new HashMap<>(), userEntity);
//    }
//
//    public String generateToken(Map<String, Object> extraClaims, UserEntity userEntity) {
//        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
//
//        String accessToken =  JWT.create()
//                .withSubject(userEntity.getUsername())
//                .withIssuedAt(new Date(System.currentTimeMillis()))
//                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
//                .withClaim("name", userEntity.getUsername())
//                .withClaim("role", String.valueOf(userEntity.getRole()))
//                .sign(algorithm);
//        TokenEntity tokenEntity = TokenEntity.builder()
//                .user_name(userEntity.getUsername())
//                .accessToken(accessToken)
//                .refreshToken(null)
//                .build();
//        tokenService.saveToken(tokenEntity);
//
//
//
//        return accessToken;
//    }
//
//    public Boolean isTokenValid(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//
//    private Boolean isTokenExpired(String token) {
//        final Date expirationDate = extractExpiration(token);
//        return expirationDate != null && expirationDate.before(new Date());
//    }
//
//    private Date extractExpiration(String token) {
//        final DecodedJWT decodedJWT = JWT.decode(token);
//        return decodedJWT.getExpiresAt();
//    }
//
//    public Long getCreatedDateFromToken(String jwtToken) {
//        final DecodedJWT decodedJWT = JWT.decode(jwtToken);
//        return decodedJWT.getIssuedAt().getTime();
//    }
//}


package com.kwaski.auth.service.auth;
import com.kwaski.auth.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {

    @Value("${jwt.accessToken.signInKey}")
    private String SECRET_KEY;

    @Value("${jwt.accessToken.accessTokenExpirationTime}")
    private Long ACCESS_TOKEN_EXPIRATION_TIME;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return keyBytes.length == 0 ? null : Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserEntity userDetails) {

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("name", userDetails.getUsername());
        claims.put("role", userDetails.getRole());
        return generateToken(claims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserEntity userDetails) {

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME)) // 10 hours
                .signWith(getSignInKey())
                .compact();
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Long getCreatedDateFromToken(String jwtToken) {
        return extractClaim(jwtToken, Claims::getIssuedAt).getTime();
    }
}


