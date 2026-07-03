package backend.datn.controller;

import backend.datn.dto.ApiResponse;
import backend.datn.entity.User;
import backend.datn.repository.UserRepository;
import backend.datn.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<?>> getProfile(
            @RequestHeader("Authorization") String bearerToken
    ) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring(7);
        }
        Long userId = jwtService.getUserIdFromToken(bearerToken);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "User not found", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "User profile retrieved successfully", user));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<?>> updateProfile(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody User updatedUser
    ) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring(7);
        }
        Long userId = jwtService.getUserIdFromToken(bearerToken);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "User not found", null));
        }
        user.setFullName(updatedUser.getFullName());
        user.setPhone(updatedUser.getPhone());
        user.setNameVehicle(updatedUser.getNameVehicle());
        user.setLincensePlate(updatedUser.getLincensePlate());
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse<>(true, "User profile updated successfully", user));
    }
}
