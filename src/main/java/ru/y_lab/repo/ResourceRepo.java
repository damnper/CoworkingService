package ru.y_lab.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
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
     * Retrieves all resources from the repository.
     *
     * @return a list of all resources
     */
    @Query(value = "SELECT * FROM coworking_service.resources", nativeQuery = true)
    List<Resource> findAllResources();

    /**
     * Updates an existing resource in the repository.
     *
     * @param id the ID of the resource to update
     * @param userId the new user ID associated with the resource
     * @param name the new resourceName of the resource
     * @param type the new resourceType of the resource
     * @return the number of rows affected
     */
    @Modifying
    @Query(value = """
                    UPDATE coworking_service.resources
                    SET user_id = :userId, name = :name, type = :type
                    WHERE id = :id
                    """, nativeQuery = true)
    Optional<Resource> updateResource(@Param("id") Long id, @Param("ownerId") Long userId, @Param("resourceName") String name, @Param("type") String type);

    /**
     * Deletes a resource from the repository by its ID.
     *
     * @param id the ID of the resource to be deleted
     * @return the number of rows affected
     */
    @Modifying
    @Query(value = """
                    DELETE FROM coworking_service.resources
                    WHERE id = :id
                    """, nativeQuery = true)
    void deleteResource(@Param("id") Long id);
}
