package com.eventify.app.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtService {

	private static final String SECRET_KEY = "3cf7ff7e1f14e47d43bab57653a80512459106b707946a88098975a07ac381fd";

	private static final long jwtExpiration = 86400000;

	private static final long refreshExpiration = 604800000;

	public String extractUsername(String jwt) {
		return extractClaim(jwt, Claims::getSubject);
	}

	public boolean isTokenValid(String jwt, UserDetails user) {
		final String username = extractUsername(jwt);
		return ((username.equals(user.getUsername())) && !isTokenExpired(jwt));
	}

	private boolean isTokenExpired(String jwt) {
		return extractExpiration(jwt).before(new Date());
	}

	public Date extractExpiration(String jwt) {
		return extractClaim(jwt, Claims::getExpiration);
	}

	public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(jwt);
		return claimsResolver.apply(claims);
	}

	public Claims extractAllClaims(String jwt) {
		return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(jwt).getBody();
	}

	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	  }

	public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		return buildToken(extraClaims, userDetails, jwtExpiration);
	}

	public String generateRefreshToken(UserDetails userDetails) {
		return buildToken(new HashMap<>(), userDetails, refreshExpiration);
	}

	private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
		return Jwts
				.builder()
				.setClaims(extraClaims)
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256)
				.compact();
	}
}
