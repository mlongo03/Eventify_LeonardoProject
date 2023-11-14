package com.eventify.app.model.json;

public record ResetPasswordRequest(String otp, String password, String confirmPassword) {
}
