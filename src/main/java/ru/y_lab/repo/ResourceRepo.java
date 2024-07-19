package ru.y_lab.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.y_lab.dto.ResourceWithOwnerDTO;
import ru.y_lab.model.Resource;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Resource entities.
 * This interface extends JpaRepository and provides methods
 * to interact with the database using SQL queries.
 */
@Repository
public interface ResourceRepo extends JpaRepository<Resource, Long> {

    /**
     * Finds a resource along with its owner by the resource's ID.
     *
     * @param resourceId the ID of the resource
     * @return an {@link Optional} containing a {@link ResourceWithOwnerDTO} if found, otherwise empty
     */
    @Query(value = """
            SELECT u.id as ownerId,
                   r.id as resourceId,
                   r.name as resourceName,
                   r.type as resourceType,
                   u.username as ownerName
            FROM coworking_service.resources r
            JOIN coworking_service.users u
                ON r.user_id = u.id
            WHERE r.id = :resourceId""", nativeQuery = true)
    Optional<ResourceWithOwnerDTO> findResourceWithOwnerById(@Param("resourceId") Long resourceId);

    /**
     * Retrieves all resources along with their owners.
     *
     * @return a {@link List} of {@link ResourceWithOwnerDTO} representing all resources and their owners
     */
    @Query(value = """
            SELECT u.id as ownerId,
                   r.id as resourceId,
                   r.name as resourceName,
                   r.type as resourceType,
                   u.username as ownerName
            FROM coworking_service.resources r
            JOIN coworking_service.users u
                ON r.user_id = u.id""", nativeQuery = true)
    List<ResourceWithOwnerDTO> findAllResourcesWithOwners();
}
