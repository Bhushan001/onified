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

    private void assignRolesToUser(String userId, Set<String> roles) {
        RealmResource realmResource = keycloak.realm(realm);
        UserResource userResource = realmResource.users().get(userId);
        RolesResource rolesResource = realmResource.roles();
        // Fetch all role representations
        java.util.List<RoleRepresentation> roleReps = roles.stream()
            .map(roleName -> rolesResource.get(roleName).toRepresentation())
            .collect(Collectors.toList());
        // Assign roles at realm level
        userResource.roles().realmLevel().add(roleReps);
    }
} 