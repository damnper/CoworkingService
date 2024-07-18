package ru.y_lab.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.y_lab.model.User;

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

}
