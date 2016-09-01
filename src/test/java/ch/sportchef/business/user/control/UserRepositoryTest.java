package ch.sportchef.business.user.control;

import ch.sportchef.business.exception.ExpectationFailedException;
import ch.sportchef.business.user.entity.User;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.NotNull;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserRepositoryTest {

    private User createUser(@NotNull final UserRepository userRepository) {
        return createUser(userRepository, "john.doe@sportchef.ch");
    }

    private User createUser(@NotNull final UserRepository userRepository, @NotNull final String email) {
        final User baseUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .phone("+41 79 555 00 01")
                .email(email)
                .build();

        return userRepository.create(baseUser);
    }

    @Test
    public void createEmailAddressUniqueTest() {
        // arrange
        final UserRepository userRepository = new UserRepository();

        // act && assert
        createUser(userRepository);
        assertThrows(ExpectationFailedException.class,
                () -> createUser(userRepository));
    }

    @Test
    public void updateOK() {
        // arrange
        final UserRepository userRepository = new UserRepository();
        final User createdUser = createUser(userRepository);
        final User userToUpdate = createdUser.toBuilder()
                .firstName("Jane")
                .build();

        // act
        final User updatedUser =  userRepository.update(userToUpdate);

        // assert
        assertThat(updatedUser.getFirstName(), is(equalTo(userToUpdate.getFirstName())));
        assertThat(updatedUser.getVersion(), is(not(equalTo(userToUpdate.getVersion()))));
    }

    @Test
    public void updateWithConflict() {
        // arrange
        final UserRepository userRepository = new UserRepository();
        final User createdUser = createUser(userRepository);
        final User userToUpdate1 = createdUser.toBuilder()
                .firstName("Jane")
                .build();
        final User userToUpdate2 = createdUser.toBuilder()
                .firstName("Jill")
                .build();

        // act & assert
        userRepository.update(userToUpdate1);
        assertThrows(ConcurrentModificationException.class,
                () -> userRepository.update(userToUpdate2));
    }

    @Test
    public void findByUserIdFound() {
        // arrange
        final UserRepository userRepository = new UserRepository();
        final User user = createUser(userRepository);

        // act
        final Optional<User> userOptional = userRepository.findByUserId(user.getUserId());

        // assert
        assertThat(userOptional.isPresent(), is(true));
        assertThat(userOptional.get(), is(user));
    }

    @Test
    public void findByUserIdNotFound() {
        // arrange
        final UserRepository userRepository = new UserRepository();

        // act
        final Optional<User> userOptional = userRepository.findByUserId(1L);

        // assert
        assertThat(userOptional.isPresent(), is(false));
    }

    @Test
    public void findAllFound() {
        // arrange
        final UserRepository userRepository = new UserRepository();
        final User user1 = createUser(userRepository, "john.doe.1@sportchef.ch");
        final User user2 = createUser(userRepository, "john.doe.2@sportchef.ch");

        // act
        final List<User> userList = userRepository.findAll();

        // assert
        assertThat(userList, notNullValue());
        assertThat(userList.size(), is(2));
        assertThat(userList.get(0), is(user1));
        assertThat(userList.get(1), is(user2));
    }

    @Test
    public void findAllNotFound() {
        // arrange
        final UserRepository userRepository = new UserRepository();

        // act
        final List<User> userList = userRepository.findAll();

        // assert
        assertThat(userList, notNullValue());
        assertThat(userList.size(), is(0));
    }

    @Test
    public void deleteExistingUser() {
        // arrange
        final UserRepository userRepository = new UserRepository();
        final User user = createUser(userRepository);

        // act
        userRepository.delete(user.getUserId());

        // assert
        assertThat(userRepository.findByUserId(user.getUserId()), is(Optional.empty()));
    }

    @Test
    public void deleteNonExistingUser() {
        // arrange
        final UserRepository userRepository = new UserRepository();

        // act
        userRepository.delete(1L);

        // assert
        assertThat(userRepository.findByUserId(1L), is(Optional.empty()));
    }

}