package com.onified.ai.authentication_service.service;

import com.onified.ai.authentication_service.dto.UserCreateRequest;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public String createUserInKeycloak(UserCreateRequest user) {
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(user.getUsername());
        userRep.setEmail(user.getEmail());
        userRep.setFirstName(user.getFirstName());
        userRep.setLastName(user.getLastName());
        userRep.setEnabled(true);

        // Set password
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(user.getPassword());
        userRep.setCredentials(Collections.singletonList(passwordCred));

        Response response = usersResource.create(userRep);
        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatusInfo());
        }

        // Extract Keycloak user ID from the Location header
        String location = response.getHeaderString("Location");
        String keycloakUserId = location != null ? location.replaceAll(".*/([^/]+)$", "$1") : null;
        response.close();

        // Assign roles if present
        if (keycloakUserId != null && user.getRoles() != null && !user.getRoles().isEmpty()) {
            assignRolesToUser(keycloakUserId, user.getRoles());
        }

        return keycloakUserId;
    }

    /**
     * Delete a user from Keycloak by username
     * @param username The username of the user to delete
     * @return true if user was deleted, false if user was not found
     */
    public boolean deleteUserFromKeycloak(String username) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();

            // Search for user by username
            List<UserRepresentation> users = usersResource.search(username, true);
            
            if (users.isEmpty()) {
                System.out.println("User '" + username + "' not found in Keycloak");
                return false;
            }

            // Find the exact match
            UserRepresentation userToDelete = users.stream()
                .filter(user -> username.equals(user.getUsername()))
                .findFirst()
                .orElse(null);

            if (userToDelete == null) {
                System.out.println("User '" + username + "' not found in Keycloak");
                return false;
            }

            // Delete the user - this method returns void, not Response
            usersResource.delete(userToDelete.getId());
            
            System.out.println("Successfully deleted user '" + username + "' from Keycloak");
            return true;

        } catch (Exception e) {
            System.err.println("Error deleting user '" + username + "' from Keycloak: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a user from Keycloak by Keycloak user ID
     * @param keycloakUserId The Keycloak user ID
     * @return true if user was deleted, false if user was not found
     */
    public boolean deleteUserFromKeycloakById(String keycloakUserId) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            UserResource userResource = usersResource.get(keycloakUserId);

            // Check if user exists
            try {
                UserRepresentation user = userResource.toRepresentation();
                if (user == null) {
                    System.out.println("User with ID '" + keycloakUserId + "' not found in Keycloak");
                    return false;
                }
            } catch (Exception e) {
                System.out.println("User with ID '" + keycloakUserId + "' not found in Keycloak");
                return false;
            }

            // Delete the user - this method returns void, not Response
            userResource.remove();
            
            System.out.println("Successfully deleted user with ID '" + keycloakUserId + "' from Keycloak");
            return true;

        } catch (Exception e) {
            System.err.println("Error deleting user with ID '" + keycloakUserId + "' from Keycloak: " + e.getMessage());
            return false;
        }
    }

    private void assignRolesToUser(String userId, Set<String> roles) {
        try {
            System.out.println("Assigning roles to user " + userId + ": " + roles);
            
            RealmResource realmResource = keycloak.realm(realm);
            UserResource userResource = realmResource.users().get(userId);
            RolesResource rolesResource = realmResource.roles();
            
            // Fetch all role representations
            java.util.List<RoleRepresentation> roleReps = roles.stream()
                .map(roleName -> {
                    try {
                        System.out.println("Fetching role representation for: " + roleName);
                        RoleRepresentation roleRep = rolesResource.get(roleName).toRepresentation();
                        System.out.println("Successfully fetched role: " + roleName + " -> " + roleRep.getName());
                        return roleRep;
                    } catch (Exception e) {
                        System.err.println("Failed to fetch role " + roleName + ": " + e.getMessage());
                        throw e;
                    }
                })
                .collect(Collectors.toList());
            
            System.out.println("Assigning " + roleReps.size() + " roles to user " + userId);
            
            // Assign roles at realm level
            userResource.roles().realmLevel().add(roleReps);
            
            System.out.println("Successfully assigned roles to user " + userId);
            
        } catch (Exception e) {
            System.err.println("Failed to assign roles to user " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to assign roles to user: " + e.getMessage(), e);
        }
    }

    /**
     * Assign roles to an existing user in Keycloak by username
     * @param username The username of the user
     * @param roles The roles to assign
     */
    public void assignRolesToExistingUser(String username, Set<String> roles) {
        try {
            System.out.println("Assigning roles to existing user " + username + ": " + roles);
            
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            
            // Search for user by username
            List<UserRepresentation> users = usersResource.search(username, true);
            
            if (users.isEmpty()) {
                throw new RuntimeException("User not found in Keycloak: " + username);
            }
            
            // Find the exact match
            UserRepresentation user = users.stream()
                .filter(u -> username.equals(u.getUsername()))
                .findFirst()
                .orElse(null);
            
            if (user == null) {
                throw new RuntimeException("User not found in Keycloak: " + username);
            }
            
            System.out.println("Found existing user in Keycloak: " + user.getId());
            
            // Assign roles using the user ID
            assignRolesToUser(user.getId(), roles);
            
        } catch (Exception e) {
            System.err.println("Failed to assign roles to existing user " + username + ": " + e.getMessage());
            throw new RuntimeException("Failed to assign roles to existing user: " + e.getMessage(), e);
        }
    }
} 