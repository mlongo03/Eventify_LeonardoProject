package com.eventify.app.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.eventify.app.model.User;


@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final JwtService jwtService;
  private final UserService userService;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

      Cookie accessTokenCookie = new Cookie("access_token", null);
      accessTokenCookie.setHttpOnly(true);
      accessTokenCookie.setPath("/");
      accessTokenCookie.setMaxAge(0);
      response.addCookie(accessTokenCookie);

      Cookie refreshTokenCookie = new Cookie("refresh_token", null);
      refreshTokenCookie.setHttpOnly(true);
      refreshTokenCookie.setPath("/");
      refreshTokenCookie.setMaxAge(0);
      response.addCookie(refreshTokenCookie);

      String refreshToken = refreshTokenCookie.getValue();
      String userEmail = jwtService.extractUsername(refreshToken);

      Optional<User> user = this.userService.findByEmail(userEmail);

      user.get().setRefreshToken(null);

      userService.update(user.get().getId(), user.get());


      SecurityContextHolder.clearContext();
  }
}
