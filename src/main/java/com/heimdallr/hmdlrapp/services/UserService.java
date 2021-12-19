package com.heimdallr.hmdlrapp.services;

import com.heimdallr.hmdlrapp.exceptions.*;
import com.heimdallr.hmdlrapp.models.User;
import com.heimdallr.hmdlrapp.repository.UsersRepository;
import com.heimdallr.hmdlrapp.services.DI.Service;
import com.heimdallr.hmdlrapp.utils.HmdlrCryptio;
import com.heimdallr.hmdlrapp.utils.Validators;

import java.util.Objects;

/**
 * Service class used to manipulate data associated with the user.
 * Singleton class maintained by HmdlrDI class.
 */
@Service(AssociatedRepo = UsersRepository.class)
public class UserService {
    private final UsersRepository usersRepository;

    private User currentUser;

    /**
     * Private default constructor
     * Receives through its arguments the instantiated Users Repository class
     *
     * @param usersRepository Instantiated Users repository class.
     */
    private UserService(Object usersRepository) {
        this.usersRepository = (UsersRepository) usersRepository;
    }

    /**
     * Method called when the user completes the first step in the user creation
     * flow.
     *
     * @param username          provided username
     * @param email             provided email
     * @param plainTextPassword provided password
     * @throws ValueExistsException When username or email is already taken
     */
    public void completeFirstStep(String username, String email, String plainTextPassword, String password2) throws ValueExistsException, ValidationException, NoEmptyFieldsException, WrongInputException {

        if(username.isBlank() || email.isBlank() || plainTextPassword.isBlank())
            throw new NoEmptyFieldsException("All fields must be completed!");

        if(!Objects.equals(plainTextPassword, password2)) {
            throw new WrongInputException("Passwords don't match!");
        }

        if (this.findByEmail(email) == null) {
            if (this.findByUsername(username) == null) {
                // No account exists with this username or email

                if(!Validators.validateEmail(email)) throw new ValidationException("Your email is not valid!");

                currentUser = new User(this.usersRepository.getNextAvailableId(),
                        email, username, HmdlrCryptio.hashPassword(plainTextPassword));
            } else throw new ValueExistsException("The username: " + username + " is already registered!");
        } else throw new ValueExistsException("The email: " + email + " is already registered!");
    }

    /**
     * Method called when the user completes the register process.
     * This method will save the user in DB.
     * @param firstName User's provided first name
     * @param lastName User's provided last name
     * @throws NoEmptyFieldsException If the first or last name fields are empty
     */
    public void completeRegister(String firstName, String lastName) throws NoEmptyFieldsException {
        if(firstName.isBlank() || lastName.isBlank()) throw new NoEmptyFieldsException("You must provide both names!");
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        this.usersRepository.addOne(currentUser);
    }

    /**
     * Method called when the user wants to authenticate using a username and a
     * password.
     * @param username Provided username
     * @param plainTextPassword Provided password
     * @throws NotFoundException If the username is not associated with any account
     * @throws WrongCredentialsException If the password is wrong for the provided username
     */
    public void authenticate(String username, String plainTextPassword) throws NotFoundException, WrongCredentialsException {
        User usernameAssociatedUser = this.findByUsername(username);
        if(usernameAssociatedUser == null) throw new NotFoundException("No user found with the username: " + username);
        if(!HmdlrCryptio.comparePasswords(plainTextPassword, usernameAssociatedUser.getHash()))
            throw new WrongCredentialsException("The password is wrong for this username.");

        // If all good, we are logged in.
        currentUser = usernameAssociatedUser;
    }

    /**
     * Method that returns the user associated with the given ID.
     *
     * @param id Searched user's id
     * @return The searched User object.
     */
    public User findById(int id) {
        return usersRepository.findById(id);
    }

    /**
     * Method that returns the user associated with the given username.
     *
     * @param username Searched user's username
     * @return The searched User object.
     */
    public User findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    /**
     * Method that returns the user associated with the given email.
     *
     * @param email Searched user's id
     * @return The searched User object.
     */
    public User findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }
}
