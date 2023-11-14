package com.eventify.app.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import com.eventify.app.model.Photo;
import com.eventify.app.model.User;
import com.eventify.app.model.enums.Role;
import com.eventify.app.model.json.AuthenticationResponse;
import com.eventify.app.model.json.LoginRequest;
import com.eventify.app.model.json.RegisterRequest;
import com.eventify.app.validator.ObjectsValidator;
import com.eventify.app.validator.UserValidator;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import io.jsonwebtoken.io.IOException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {

	private final UserService userService;
    private final UserValidator userValidator;
    private final PhotoService photoService;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuthenticationManager authManager;
    private final ObjectsValidator<RegisterRequest> validator;

    @Transactional
    public String signUp(RegisterRequest request) throws Exception {
        validator.validate(request);
        if (request.isCheckbox() == false) {
            return ("accept terms and conditions");
        }
        String errorMessage = null;
        if ((errorMessage = userValidator.isFormValid(request)) != null) {
            return (errorMessage);
        }
        User user = new User(request.getFirstname(), request.getLastname(), request.getDob(), request.getEmail(), request.getPassword(), null);
        try {
            user.setRole(Role.USER);
            userService.create(user);
        } catch (DataIntegrityViolationException e) {

            return ("Email already registered");
        }
        Photo profilePicture = photoService.uploadPhoto(request.getProfilePicture());
        photoService.create(profilePicture);
        user.setProfilePicture(profilePicture);
        userService.update(user.getId(), user);
        return ("Registered Succesfully");
    }

    public AuthenticationResponse signIn(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        String accessToken = null;
        String refreshToken = null;
        String errorMessage = null;
		Date expirationDate = null;
        try {
            Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Optional<User> user = userService.findByEmail(loginRequest.getEmail());
            if (user.isEmpty()) {
                errorMessage = "Email not registered";
                return AuthenticationResponse.builder().error(errorMessage).accessToken(accessToken).refreshToken(refreshToken).build();
            }

            try {
                String secretKey = generateSecretKey();
                int otp = generateOtp(secretKey);
                emailService.sendSignInEmail(loginRequest.getEmail(), otp);
                user.get().setOtp(otp);
                userService.update(user.get().getId(), user.get());
            } catch (MessagingException e) {
                errorMessage = "Bad Credentials";
                return AuthenticationResponse.builder().error(errorMessage).accessToken(accessToken).refreshToken(refreshToken).build();
            }
            return AuthenticationResponse.builder().error(errorMessage).accessToken(accessToken).refreshToken(refreshToken).expirationDate(expirationDate).email(user.get().getEmail()).build();
        } catch (BadCredentialsException e) {
            errorMessage = "Bad Credentials";
            return AuthenticationResponse.builder().error(errorMessage).accessToken(accessToken).refreshToken(refreshToken).build();
        }
    }

    public ResponseEntity<AuthenticationResponse> refreshToken( HttpServletRequest request, HttpServletResponse response) throws IOException {
		String refreshToken;
		String userEmail;
		String accessToken;
		Date expirationDate;
        Cookie tokenCookie = WebUtils.getCookie(request, "refresh_token");

        if (tokenCookie == null) {
        	return ResponseEntity.ok(AuthenticationResponse.builder().error("invalid Refresh-token").refreshToken(null).accessToken(null).build());
        }

        refreshToken = tokenCookie.getValue();
        userEmail = jwtService.extractUsername(refreshToken);

		if (userEmail != null) {
			var user = this.userService.findByEmail(userEmail)
					.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
			if (refreshToken.equals(user.refreshToken)) {
				accessToken = jwtService.generateToken(user);
				Cookie accessTokenCookie = new Cookie("access_token", accessToken);
				accessTokenCookie.setHttpOnly(true);
				accessTokenCookie.setPath("/");
				accessTokenCookie.setSecure(true);
				response.addCookie(accessTokenCookie);
				expirationDate = jwtService.extractExpiration(accessToken);
				return ResponseEntity.ok(AuthenticationResponse.builder().error(null).refreshToken(null).accessToken(accessToken).expirationDate(expirationDate).build());
			}
		}
        return ResponseEntity.ok(AuthenticationResponse.builder().error("invalid Refresh-token").refreshToken(null).accessToken(null).build());
	}

    public Integer generateOtp(String secretKey) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        return gAuth.getTotpPassword(secretKey);
    }

    public String generateSecretKey() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }
}
