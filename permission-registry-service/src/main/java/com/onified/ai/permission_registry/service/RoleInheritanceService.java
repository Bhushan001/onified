package com.onified.ai.permission_registry.service;

import com.onified.ai.permission_registry.constants.ErrorMessages;
import com.onified.ai.permission_registry.entity.Role;
import com.onified.ai.permission_registry.entity.RoleInheritance;
import com.onified.ai.permission_registry.exception.BadRequestException;
import com.onified.ai.permission_registry.exception.ConflictException;
import com.onified.ai.permission_registry.exception.ResourceNotFoundException;
import com.onified.ai.permission_registry.repository.RoleInheritanceRepository;
import com.onified.ai.permission_registry.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RoleInheritanceService {

    private final RoleInheritanceRepository roleInheritanceRepository;
    private final RoleRepository roleRepository; // To access Role entities for validation
    private final int MAX_INHERITANCE_DEPTH = 3; // From documentation

    @Autowired
    public RoleInheritanceService(RoleInheritanceRepository roleInheritanceRepository, RoleRepository roleRepository) {
        this.roleInheritanceRepository = roleInheritanceRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Establishes a new role inheritance relationship.
     * Performs validations for existence, self-inheritance, cyclic dependencies, and max depth.
     * Updates child role's inheritance depth.
     * @param parentRoleId The ID of the parent role.
     * @param childRoleId The ID of the child role.
     * @param approvedBy The user who approved the inheritance (optional).
     * @return The created RoleInheritance entity.
     * @throws ResourceNotFoundException if parent or child role does not exist.
     * @throws ConflictException if the inheritance already exists, or a cyclic dependency is detected.
     * @throws BadRequestException if role tries to inherit from itself or max depth is exceeded.
     */
    @Transactional
    public RoleInheritance createRoleInheritance(String parentRoleId, String childRoleId, String approvedBy) {
        if (parentRoleId.equals(childRoleId)) {
            throw new BadRequestException(String.format(ErrorMessages.ROLE_INHERITANCE_SAME_ROLE, parentRoleId));
        }

        Role parentRole = roleRepository.findById(parentRoleId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ROLE_NOT_FOUND, parentRoleId)));
        Role childRole = roleRepository.findById(childRoleId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ROLE_NOT_FOUND, childRoleId)));

        RoleInheritance.RoleInheritanceId id = new RoleInheritance.RoleInheritanceId(parentRoleId, childRoleId);
        if (roleInheritanceRepository.existsById(id)) {
            throw new ConflictException(String.format(ErrorMessages.ROLE_INHERITANCE_ALREADY_EXISTS, parentRoleId, childRoleId));
        }

        // Validate for cyclic inheritance before saving
        if (isCyclic(parentRoleId, childRoleId)) {
            throw new ConflictException(String.format(ErrorMessages.ROLE_INHERITANCE_CYCLIC, childRoleId, parentRoleId));
        }

        // Calculate and validate new inheritance depth
        int newChildDepth = parentRole.getInheritanceDepth() + 1;
        if (newChildDepth > MAX_INHERITANCE_DEPTH) {
            throw new BadRequestException(String.format(ErrorMessages.ROLE_INHERITANCE_DEPTH_EXCEEDED, childRoleId));
        }

        RoleInheritance inheritance = new RoleInheritance(parentRoleId, childRoleId, approvedBy, LocalDateTime.now());
        RoleInheritance savedInheritance = roleInheritanceRepository.save(inheritance);

        // Update child role's inheritance depth in the Role entity itself
        childRole.setInheritanceDepth(newChildDepth);
        roleRepository.save(childRole); // Save the updated child role

        return savedInheritance;
    }

    /**
     * Retrieves all role inheritance relationships.
     * @return A list of all RoleInheritance entities.
     */
    public List<RoleInheritance> getAllRoleInheritances() {
        return roleInheritanceRepository.findAll();
    }

    /**
     * Retrieves role inheritance relationships where a role is a parent.
     * @param parentRoleId The ID of the parent role.
     * @return A list of RoleInheritance entities.
     */
    public List<RoleInheritance> getChildrenOfRole(String parentRoleId) {
        if (!roleRepository.existsById(parentRoleId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ROLE_NOT_FOUND, parentRoleId));
        }
        return roleInheritanceRepository.findByParentRoleId(parentRoleId);
    }

    /**
     * Retrieves role inheritance relationships where a role is a child.
     * @param childRoleId The ID of the child role.
     * @return A list of RoleInheritance entities.
     */
    public List<RoleInheritance> getParentsOfRole(String childRoleId) {
        if (!roleRepository.existsById(childRoleId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ROLE_NOT_FOUND, childRoleId));
        }
        return roleInheritanceRepository.findByChildRoleId(childRoleId);
    }

    /**
     * Deletes a role inheritance relationship.
     * @param parentRoleId The ID of the parent role.
     * @param childRoleId The ID of the child role.
     * @throws ResourceNotFoundException if the inheritance relationship does not exist.
     */
    @Transactional
    public void deleteRoleInheritance(String parentRoleId, String childRoleId) {
        RoleInheritance.RoleInheritanceId id = new RoleInheritance.RoleInheritanceId(parentRoleId, childRoleId);
        if (!roleInheritanceRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ROLE_INHERITANCE_NOT_FOUND, parentRoleId, childRoleId));
        }
        roleInheritanceRepository.deleteById(id);

        // Re-calculate and update child role's inheritance depth (and potentially its children)
        // This is a complex operation in a real system (BFS/DFS to find longest path to root)
        // For simplicity, we'll set it to 0 if no other parents exist, or re-calculate based on longest path from roots.
        Role childRole = roleRepository.findById(childRoleId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ROLE_NOT_FOUND, childRoleId))); // Should not happen if inheritance existed

        List<RoleInheritance> remainingParents = roleInheritanceRepository.findByChildRoleId(childRoleId);
        if (remainingParents.isEmpty()) {
            childRole.setInheritanceDepth(0); // No more parents, reset to base depth
        } else {
            // Find the maximum depth of its new parents and add 1
            int maxParentDepth = remainingParents.stream()
                    .map(ri -> roleRepository.findById(ri.getParentRoleId()).orElse(new Role()).getInheritanceDepth())
                    .max(Integer::compare)
                    .orElse(0); // If no parent found (shouldn't happen here), default to 0
            childRole.setInheritanceDepth(maxParentDepth + 1);
        }
        roleRepository.save(childRole);
        // A more advanced solution would propagate depth recalculation down the entire child hierarchy.
    }


    /**
     * Checks for cyclic inheritance by performing a DFS from the potential child.
     * If adding (parent, child) creates a cycle, it means 'parent' is reachable from 'child'.
     * @param potentialParent The parent role ID being considered.
     * @param potentialChild The child role ID being considered.
     * @return true if a cycle is detected, false otherwise.
     */
    private boolean isCyclic(String potentialParent, String potentialChild) {
        // Build an adjacency list representing the current inheritance graph
        Map<String, List<String>> adj = new HashMap<>();
        List<RoleInheritance> allInheritances = roleInheritanceRepository.findAll();
        for (RoleInheritance ri : allInheritances) {
            adj.computeIfAbsent(ri.getParentRoleId(), k -> new ArrayList<>()).add(ri.getChildRoleId());
        }
        // Add the potential new edge
        adj.computeIfAbsent(potentialParent, k -> new ArrayList<>()).add(potentialChild);

        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();

        // Start DFS from the potential child to see if it can reach the potential parent
        return dfsForCycleDetection(potentialChild, potentialParent, adj, visited, recursionStack);
    }

    private boolean dfsForCycleDetection(String current, String target, Map<String, List<String>> adj,
                                         Set<String> visited, Set<String> recursionStack) {
        visited.add(current);
        recursionStack.add(current);

        for (String neighbor : adj.getOrDefault(current, Collections.emptyList())) {
            if (neighbor.equals(target)) { // If we reach the target, a cycle exists
                return true;
            }
            if (!visited.contains(neighbor)) {
                if (dfsForCycleDetection(neighbor, target, adj, visited, recursionStack)) {
                    return true;
                }
            } else if (recursionStack.contains(neighbor)) {
                // This means a cycle already exists within the graph itself, not necessarily involving the new edge directly to `target`
                // But if we're doing a general cycle check after adding a new edge, this branch might also indicate a problem
                // For direct check: if neighbor is target, return true. Otherwise, this is a cycle not relevant to THIS specific (parent, child) addition.
            }
        }

        recursionStack.remove(current);
        return false;
    }

    // TODO: Implement SoD validation if needed during inheritance.
    // private void validateSoD(String parentRoleId, String childRoleId) { ... }
}

