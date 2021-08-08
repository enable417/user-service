package ru.evotor.userservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.evotor.userservice.entity.UserEntity;
import ru.evotor.userservice.exception.UserNotFoundException;
import ru.evotor.userservice.model.User;
import ru.evotor.userservice.repository.UserRepo;
import ru.evotor.userservice.wrapper.DateRange;
import ru.evotor.userservice.wrapper.FullName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserService {

    private final UserRepo userRepo;

    private static final String BLANK_ARGUMENTS_EXCEPTION_MESSAGE = "Arguments can not be empty or blank";

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public List<User> getAllUsers() throws UserNotFoundException {
        List<User> allUsers = new ArrayList<>();
        userRepo.findAll().forEach(userEntity -> allUsers.add(User.toModel(userEntity)));

        if (allUsers.isEmpty()) {
            throw new UserNotFoundException("No users in database");
        }

        return allUsers;
    }

    public User createUser(User user) {
        return User.toModel(userRepo.save(User.toEntity(user)));
    }

    public User getUserById(Long id) throws UserNotFoundException {
        return User.toModel(userRepo.findById(id).orElseThrow(() -> new UserNotFoundException("No user with such id")));
    }

    public List<User> getUsersByFullNameParts(FullName fullName) throws UserNotFoundException {
        List<User> users;

        String firstName = fullName.getFirstName();
        String lastName = fullName.getLastName();
        String patronymic = fullName.getPatronymic();

        users = getUsersByFullNameParts(firstName, lastName, patronymic);

        if (users.isEmpty()) {
            throw new UserNotFoundException("No users with such parameters");
        }

        return users;
    }

    public List<User> getUsersByDateOfBirthRange(DateRange dateOfBirthRange) throws UserNotFoundException {
        Date dateFrom = dateOfBirthRange.getDateFrom();
        Date dateTo = dateOfBirthRange.getDateTo();

        if (dateFrom == null && dateTo == null) {
            throw new IllegalArgumentException("Arguments can not be null");
        }

        List<User> users = new ArrayList<>();

        if (dateFrom != null && dateTo != null && !dateFrom.equals(dateTo)) {
            userRepo.findByDateOfBirthBetween(dateFrom, dateTo).forEach(userEntity ->
                    users.add(User.toModel(userEntity)));
        } else if (dateFrom != null && dateTo == null) {
            userRepo.findByDateOfBirth(dateFrom).forEach(userEntity -> users.add(User.toModel(userEntity)));
        } else if (dateFrom == null) {
            userRepo.findByDateOfBirth(dateTo).forEach(userEntity -> users.add(User.toModel(userEntity)));
        } else {
            userRepo.findByDateOfBirth(dateFrom).forEach(userEntity -> users.add(User.toModel(userEntity)));
        }

        if (users.isEmpty()) {
            throw new UserNotFoundException("No users with such parameters");
        }

        return users;
    }

    public User updateUser(User user) throws UserNotFoundException {
        UserEntity userToUpdate = userRepo.findById(user.getId()).orElseThrow(() ->
                new UserNotFoundException("No user with such id"));

        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String patronymic = user.getPatronymic();
        Date dateOfBirth = user.getDateOfBirth();

        if (firstName != null) {
            userToUpdate.setFirstName(firstName);
        }
        if (lastName != null) {
            userToUpdate.setLastName(lastName);
        }
        if (patronymic != null) {
            userToUpdate.setPatronymic(patronymic);
        }
        if (dateOfBirth != null) {
            userToUpdate.setDateOfBirth(dateOfBirth);
        }
        userRepo.save(userToUpdate);

        return User.toModel(userToUpdate);
    }

    public Long deleteUser(Long id){
        userRepo.deleteById(id);
        return id;
    }

    private List<User> getUsersByFullNameParts(String firstName, String lastName, String patronymic) {
        List<User> users = new ArrayList<>();

        if (firstName != null && lastName != null && patronymic != null) {
            addUsersByFullNameToList(users, firstName, lastName, patronymic);
        } else if (firstName != null && lastName != null) {
            addUsersByFirstNameAndLastNameToList(users, firstName, lastName);
        } else if (firstName != null && patronymic != null) {
            addUsersByFirstNameAndPatronymicToList(users, firstName, patronymic);
        } else if (firstName != null) {
            addUsersByFirstNameToList(users, firstName);
        } else if (lastName != null) {
            addUsersByLastNameToList(users, lastName);
        } else if (patronymic != null) {
            addUsersByPatronymicToList(users, patronymic);
        } else {
            throw new IllegalArgumentException("Can not find user with such parameters");
        }

        return users;
    }

    private void addUsersByFullNameToList(List<User> usersList, String firstName, String lastName, String patronymic) {
        if (firstName.isBlank() || lastName.isBlank() || patronymic.isBlank()) {
            throw new IllegalArgumentException(BLANK_ARGUMENTS_EXCEPTION_MESSAGE);
        }

        userRepo.findByFirstNameAndLastNameAndPatronymic(firstName, lastName, patronymic).forEach(userEntity ->
                usersList.add(User.toModel(userEntity)));
    }

    private void addUsersByFirstNameAndLastNameToList(List<User> usersList, String firstName, String lastName) {
        if (firstName.isBlank() || lastName.isBlank()) {
            throw new IllegalArgumentException(BLANK_ARGUMENTS_EXCEPTION_MESSAGE);
        }

        userRepo.findByFirstNameAndLastName(firstName, lastName).forEach(userEntity ->
                usersList.add(User.toModel(userEntity)));
    }

    private void addUsersByFirstNameAndPatronymicToList(List<User> usersList, String firstName, String patronymic) {
        if (firstName.isBlank() || patronymic.isBlank()) {
            throw new IllegalArgumentException(BLANK_ARGUMENTS_EXCEPTION_MESSAGE);
        }

        userRepo.findByFirstNameAndPatronymic(firstName, patronymic).forEach(userEntity ->
                usersList.add(User.toModel(userEntity)));
    }

    private void addUsersByFirstNameToList(List<User> usersList, String firstName) {
        if (firstName.isBlank()) {
            throw new IllegalArgumentException(BLANK_ARGUMENTS_EXCEPTION_MESSAGE);
        }

        userRepo.findByFirstName(firstName).forEach(userEntity -> usersList.add(User.toModel(userEntity)));
    }

    private void addUsersByLastNameToList(List<User> usersList, String lastName) {
        if (lastName.isBlank()) {
            throw new IllegalArgumentException(BLANK_ARGUMENTS_EXCEPTION_MESSAGE);
        }

        userRepo.findByLastName(lastName).forEach(userEntity -> usersList.add(User.toModel(userEntity)));
    }

    private void addUsersByPatronymicToList(List<User> usersList, String patronymic) {
        if (patronymic.isBlank()) {
            throw new IllegalArgumentException(BLANK_ARGUMENTS_EXCEPTION_MESSAGE);
        }

        userRepo.findByPatronymic(patronymic).forEach(userEntity -> usersList.add(User.toModel(userEntity)));
    }
}
