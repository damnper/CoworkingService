package ru.y_lab.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserTests {

    @Test
    public void testUserConstructorAndGetters() {
        User user = new User(1L, "testUser", "password", "ADMIN");

        assertEquals(1L, user.getId());
        assertEquals("testUser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    public void testUserSetters() {
        User user = new User();
        user.setId(2L);
        user.setUsername("newUser");
        user.setPassword("newPassword");
        user.setRole("USER");

        assertEquals(2L, user.getId());
        assertEquals("newUser", user.getUsername());
        assertEquals("newPassword", user.getPassword());
        assertEquals("USER", user.getRole());
    }

    @Test
    public void testUserBuilder() {
        User user = User.builder()
                .id(3L)
                .username("builderUser")
                .password("builderPassword")
                .role("MANAGER")
                .build();

        assertEquals(3L, user.getId());
        assertEquals("builderUser", user.getUsername());
        assertEquals("builderPassword", user.getPassword());
        assertEquals("MANAGER", user.getRole());
    }

    @Test
    public void testUserEqualsAndHashCode() {
        User user1 = new User(1L, "user1", "password1", "ADMIN");
        User user2 = new User(1L, "user1", "password1", "ADMIN");
        User user3 = new User(2L, "user2", "password2", "USER");

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertNotEquals(user2, user3);

        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    public void testUserToString() {
        User user = new User(1L, "testUser", "password", "ADMIN");
        String expectedString = "User(id=1, username=testUser, password=password, role=ADMIN)";
        assertEquals(expectedString, user.toString());
    }
}
