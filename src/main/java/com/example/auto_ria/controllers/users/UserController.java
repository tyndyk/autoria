package com.example.auto_ria.controllers.users;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.auto_ria.dto.responces.PaginatedResponse;
import com.example.auto_ria.dto.responces.ResponceObj;
import com.example.auto_ria.dto.responces.builder.ResponseBuilder;
import com.example.auto_ria.dto.updateDTO.UserUpdateDTO;
import com.example.auto_ria.models.responses.user.UserResponse;
import com.example.auto_ria.services.user.UsersServiceSQL;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "Operations for managing users, including retrieval, updates, and deletions.")
public class UserController {

        private final UsersServiceSQL usersServiceMySQL;

        @Operation(summary = "Retrieve paginated list of users", description = "Fetches a paginated list of users. Defaults to page size 10 if not specified.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
                        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters provided")
        })
        @GetMapping("/page")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ResponceObj<PaginatedResponse<UserResponse>>> getAll(
                        @RequestParam @Parameter(description = "Page number, starting from 0") int page,
                        @RequestParam(defaultValue = "10") @Parameter(description = "Number of users per page (default: 10)") int pageSize) {
                return ResponseBuilder.buildPagedResponse(usersServiceMySQL.getAll(page, pageSize));
        }

        @Operation(summary = "Get user by ID", description = "Fetches a user by their unique ID. Returns 404 if the user is not found.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User found successfully"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<ResponceObj<UserResponse>> getById(
                        @PathVariable @Parameter(description = "Unique identifier of the user") int id) {
                return ResponseBuilder.buildResponse(usersServiceMySQL.getByIdAsResponse(id));
        }

        @Operation(summary = "Partially update a user", description = "Allows partial updates to a user's details, such as name, email, etc.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request body or user ID"),
                        @ApiResponse(responseCode = "401", description = "No permission to modify user"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        @PatchMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
        public ResponseEntity<ResponceObj<UserResponse>> patchUser(
                        @PathVariable @Parameter(description = "User ID to update") int id,
                        @RequestBody @Parameter(description = "User details to update") UserUpdateDTO partialUser) {
                return ResponseBuilder.buildResponse(usersServiceMySQL.updateUser(id, partialUser));
        }

        @Operation(summary = "Update user's avatar", description = "Uploads a new profile picture for the user. Image file should be in PNG or JPEG format.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Avatar updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid file format or user not found"),
                        @ApiResponse(responseCode = "401", description = "No permission to delete user"),
                        @ApiResponse(responseCode = "413", description = "Uploaded file is too large"),
                        @ApiResponse(responseCode = "500", description = "Error uploading file")
        })
        @PatchMapping("/change-avatar/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
        public ResponseEntity<ResponceObj<String>> patchAvatar(
                        @PathVariable @Parameter(description = "User ID for which avatar is being updated") int id,
                        @RequestParam("avatar") @Parameter(description = "Avatar image file (PNG/JPEG)") MultipartFile avatar) {
                usersServiceMySQL.updateAvatar(id, avatar);
                return ResponseBuilder.buildResponse("Avatar was updated successfully");
        }

        @Operation(summary = "Delete a user", description = "Deletes a user by their ID. Only available to admins and managers.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "User not found"),
                        @ApiResponse(responseCode = "404", description = "No permission to delete user")

        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ResponceObj<String>> deleteById(
                        @PathVariable @Parameter(description = "ID of the user to delete") int id) {
                usersServiceMySQL.deleteById(id);
                return ResponseBuilder.buildResponse("User was successfully deleted");
        }
}
