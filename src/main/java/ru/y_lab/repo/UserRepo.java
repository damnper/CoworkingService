package ru.y_lab.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.y_lab.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing User entities.
 * This interface extends JpaRepository and provides methods
 * to interact with the database using SQL queries.
 */
@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user
     * @return an Optional containing the user if found, otherwise empty
     */
    @Query(value = """
                    SELECT * FROM coworking_service.users
                    WHERE username = :username
                    """, nativeQuery = true)
    Optional<User> findByUsername(@Param("username") String username);

    /**
     * Retrieves all users from the database.
     *
     * @return a list of all users
     */
    @Query(value = "SELECT * FROM coworking_service.users", nativeQuery = true)
    List<User> findAllUsers();

    /**
     * Updates the details of an existing user.
     *
     * @param username the new username of the user
     * @param password the new password of the user
     * @param role the new role of the user
     * @param id the ID of the user to update
     */
    @Modifying
    @Query(value = """
                    UPDATE coworking_service.users
                    SET username = :username, password = :password, role = :role
                    WHERE id = :id
                    """, nativeQuery = true)
    void updateUser(@Param("username") String username, @Param("password") String password, @Param("role") String role, @Param("id") Long id);

}
