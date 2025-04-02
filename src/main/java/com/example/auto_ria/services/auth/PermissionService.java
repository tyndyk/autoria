package com.example.auto_ria.services.auth;

import java.util.Arrays;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.auto_ria.dao.users.UserDaoSQL;
import com.example.auto_ria.enums.ERole;
import com.example.auto_ria.exceptions.auth.PermissionDeniedException;
import com.example.auto_ria.exceptions.auth.UnauthorizedAccessException;
import com.example.auto_ria.exceptions.user.UserNotActivatedException;
import com.example.auto_ria.models.car.CarSQL;
import com.example.auto_ria.models.user.UserSQL;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PermissionService {

    private UserDaoSQL usersService;

    public UserSQL getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            UserSQL user = usersService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new UnauthorizedAccessException("API access token is invalid"));
            return user;
        }
        throw new UnauthorizedAccessException("API access token is either missing or invalid");
    }

    public void isValidRole(String role) {
        if (!Arrays.stream(ERole.values())
                .anyMatch(eRole -> eRole.name().equalsIgnoreCase(role))) {
            throw new IllegalArgumentException("Invalid currency: " + role);
        }
    }

    public void allowedToCancelSubscription(UserSQL user) {
        UserSQL userAuth = getAuthenticatedUser();

        if (userAuth.getRoles().contains(ERole.USER) && (userAuth.getId() != user.getId())) {
            throw new UnauthorizedAccessException("You are not allowed to cancel this subscription.");
        }

        if (!user.getRoles().contains(ERole.USER)) {
            throw new PermissionDeniedException("This action is not allowed for your role.");
        }

        if (user.getPremiumPlan() == null || user.getPremiumPlan().getSubId() == null) {
            throw new UnauthorizedAccessException("No active subscription found to cancel.");
        }
    }

    private void validatePermissionToDeleteManager() {
        if (usersService.countByRole(ERole.MANAGER) < 2) {
            throw new PermissionDeniedException(
                    "At least one manager must remain. To perform the deletion anyway return to the settings");
        }
    }

    private boolean validatePermissionToModifyUserBool(UserSQL userToPatch, UserSQL userAuthenticated) {

        if (userToPatch.getRoles().contains(ERole.ADMIN_ROOT))
            throw new PermissionDeniedException("Root user cannot be deleted");

        if (userToPatch.getRoles().contains(ERole.MANAGER)) {
            validatePermissionToDeleteManager();
        }

        if (userAuthenticated.getRoles().contains(ERole.ADMIN_ROOT)) {
            return true;
        }
        if (userAuthenticated.getRoles().contains(ERole.ADMIN)) {
            if ((userToPatch.getRoles().contains(ERole.ADMIN) && (userToPatch.getId() == userAuthenticated.getId()) // themselves
                    || (userToPatch.getRoles().contains(ERole.MANAGER) || userToPatch.getRoles().contains(ERole.USER))))
                return true;
        }
        if (userAuthenticated.getRoles().contains(ERole.USER)
                || userAuthenticated.getRoles().contains(ERole.MANAGER)) {
            if (userToPatch.getId() == userAuthenticated.getId())
                return true;
        }
        return false;
    }

    public UserSQL validatePermissionToModifyUser(UserSQL userToPatch) {
        UserSQL userAuthenticated = getAuthenticatedUser();

        boolean permission = validatePermissionToModifyUserBool(userToPatch, userAuthenticated);
        if (!permission) {
            throw new PermissionDeniedException("Unauthorized: You do not have permission to delete this user");
        }
        return userAuthenticated;
    }

    public void isAuthority() {
        UserSQL user = getAuthenticatedUser();
        if (user.getRoles().contains(ERole.USER))
            throw new UnauthorizedAccessException("This action is not allowed for the role");
    }

    public boolean isAuthorityBool(UserSQL user) {
        if (!user.getRoles().contains(ERole.USER))
            return true;
        return false;
    }

    public boolean isAuthorityBoolContext() {
        UserSQL user = getAuthenticatedUser();
        return isAuthorityBool(user);
    }

    public UserSQL allowedToPostCar() {
        UserSQL user = getAuthenticatedUser();

        if (user.getCars().size() > 10) {
            validatePremiumPermission();
            throw new PermissionDeniedException("Premium plan required");
        }
        return user;
    }

    public void allowedToModifyCar(CarSQL car) {
        UserSQL user = getAuthenticatedUser();
        if (car.getUser().getId() != user.getId()) {
            throw new PermissionDeniedException("No permission to modify this car");
        }
    }

    private boolean hasRole(UserSQL user, ERole role) {
        return user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role.toString()));

    }

    public boolean validateUserPremiumPermission() {
        UserSQL user = getAuthenticatedUser();
        if (hasRole(user, ERole.USER)) {
            if (user.isPremium())
                return true;
        }
        return false;
    }

    public boolean validatePremiumPermission() {

        UserSQL user = getAuthenticatedUser();

        if (hasRole(user, ERole.USER)) {
            if (user.isPremium()) {
                return true;
            }
            return false;
        }
        return true;
    }

    public void allowedToFetchUser(UserSQL user) {
        if (user.getRoles().contains(ERole.USER) && user.isActivated()) {
            return;
        }

        UserSQL userAuth = getAuthenticatedUser();

        if (userAuth.getRoles().contains(ERole.ADMIN)) {
            return;
        }

        if (userAuth.getRoles().contains(ERole.USER)) {
            throw new PermissionDeniedException("Access denied");
        }

        if (userAuth.getRoles().contains(ERole.MANAGER)) {
            if (user.getRoles().contains(ERole.ADMIN)) {
                throw new PermissionDeniedException("Access denied");
            }

            if (user.getRoles().contains(ERole.MANAGER) && !user.isActivated()) {
                throw new UserNotActivatedException("User is not activated");
            }
        }
    }

}
