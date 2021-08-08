package ru.evotor.userservice.service;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.evotor.userservice.entity.UserEntity;
import ru.evotor.userservice.exception.UserNotFoundException;
import ru.evotor.userservice.model.User;
import ru.evotor.userservice.repository.UserRepo;
import ru.evotor.userservice.wrapper.DateRange;
import ru.evotor.userservice.wrapper.FullName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepo userRepo;

    @Test
    void getAllUsers_shouldThrowException_whenNoUsersInDataBase() {
        when(userRepo.findAll()).thenReturn(new ArrayList<>());
        assertThrows(UserNotFoundException.class, () -> userService.getAllUsers());
    }

    @Test
    void getAllUsers_shouldReturnListWithSizeEqualsOne_whenOneUserInDataBase() throws UserNotFoundException {
        List<UserEntity> oneUserList = new ArrayList<>();
        oneUserList.add(
                new UserEntity(1L, "A", "B", "C", new Date(2021)));
        when(userRepo.findAll()).thenReturn(oneUserList);

        List<User> expected = new ArrayList<>();
        expected.add(new User(1L, "A", "B", "C", new Date(2021)));

        assertEquals(expected, userService.getAllUsers());
    }

    @Test
    void getAllUsers_shouldReturnListWithSizeEqualsNumberOfUsers_whenMultipleUsersInDataBase() throws
            UserNotFoundException {
        List<UserEntity> oneUserList = new ArrayList<>();
        oneUserList.add(
                new UserEntity(1L, "A", "B", "C", new Date(2021)));
        oneUserList.add(
                new UserEntity(2L, "C", "B", "A", new Date(2022)));
        oneUserList.add(
                new UserEntity(3L, "M", "N", "K", new Date(2023)));
        when(userRepo.findAll()).thenReturn(oneUserList);

        List<User> expected = new ArrayList<>();
        expected.add(new User(1L, "A", "B", "C", new Date(2021)));
        expected.add(new User(2L, "C", "B", "A", new Date(2022)));
        expected.add(new User(3L, "M", "N", "K", new Date(2023)));

        assertEquals(expected, userService.getAllUsers());
    }

    @Test
    void getUserById_shouldThrowException_whenNoUserWithIdInDataBase() {
        Long id = 1L;
        when(userRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(id));
    }

    @Test
    void getUserById_shouldReturnUser_whenThereIsUserWithIdInDataBase() throws UserNotFoundException {
        Long id = 1L;
        when(userRepo.findById(id))
                .thenReturn(Optional.of(new UserEntity(id, "A", "B", "C", new Date(2000))));

        User expected = new User(id, "A", "B", "C", new Date(2000));

        assertEquals(expected, userService.getUserById(id));
    }

    @Test
    void getUsersByFullNameParts_shouldThrowException_whenNoSuchUserInDataBaseByFirstName() {
        String firstName = "First Name";
        when(userRepo.findByFirstName(firstName)).thenReturn(new ArrayList<>());

        FullName inputFullName = new FullName(firstName, null, null);

        assertThrows(UserNotFoundException.class, () -> userService.getUsersByFullNameParts(inputFullName));
    }

    @Test
    void getUsersByFullNameParts_shouldThrowException_whenAllFullNamePartsNull() {
        FullName inputFullName = new FullName(null, null, null);

        assertThrows(IllegalArgumentException.class, () -> userService.getUsersByFullNameParts(inputFullName));
    }

    @Test
    void getUsersByFullNameParts_shouldUseFirstNameRepositoryMethod_whenInputHasOnlyFirstName() throws
            UserNotFoundException {
        String firstName = "FirstName";
        FullName fullName = new FullName(firstName, null, null);

        List<UserEntity> expectedRepositoryOutput = new ArrayList<>();
        expectedRepositoryOutput.add(new UserEntity());
        when(userRepo.findByFirstName(firstName)).thenReturn(expectedRepositoryOutput);

        userService.getUsersByFullNameParts(fullName);
        verify(userRepo, times(1)).findByFirstName(firstName);
    }

    @Test
    void getUsersByFullNameParts_shouldUseLastNameRepositoryMethod_whenInputHasOnlyLastName() throws
            UserNotFoundException {
        String lastName = "LastName";
        FullName fullName = new FullName(null, lastName, null);

        List<UserEntity> expectedRepositoryOutput = new ArrayList<>();
        expectedRepositoryOutput.add(new UserEntity(1L, "f", lastName, "p", new Date(0)));
        when(userRepo.findByLastName(lastName)).thenReturn(expectedRepositoryOutput);

        userService.getUsersByFullNameParts(fullName);
        verify(userRepo, times(1)).findByLastName(lastName);
    }

    @Test
    void getUsersByFullNameParts_shouldUsePatronymicRepositoryMethod_whenInputHasOnlyPatronymic() throws
            UserNotFoundException {
        String patronymic = "Patronymic";
        FullName fullName = new FullName(null, null, patronymic);

        List<UserEntity> expectedRepositoryOutput = new ArrayList<>();
        expectedRepositoryOutput.add(new UserEntity(1L, "f", "l", patronymic, new Date(0)));
        when(userRepo.findByPatronymic(patronymic)).thenReturn(expectedRepositoryOutput);

        userService.getUsersByFullNameParts(fullName);
        verify(userRepo, times(1)).findByPatronymic(patronymic);
    }

    @Test
    void getUsersByFullNameParts_shouldUseFirstNameAndLastNameRepositoryMethod_whenInputHasOnlyFirstNameAndLastName()
            throws UserNotFoundException {
        String firstName = "FirstName";
        String lastName = "LastName";
        FullName fullName = new FullName(firstName, lastName, null);

        List<UserEntity> expectedRepositoryOutput = new ArrayList<>();
        expectedRepositoryOutput.add(new UserEntity(1L, firstName, lastName, "p", new Date(0)));
        when(userRepo.findByFirstNameAndLastName(firstName, lastName)).thenReturn(expectedRepositoryOutput);

        userService.getUsersByFullNameParts(fullName);
        verify(userRepo, times(1)).findByFirstNameAndLastName(firstName, lastName);
    }

    @Test
    void getUsersByFullNameParts_shouldUseFirstNameAndPatronymicRepositoryMethod_whenInputHasOnlyFirstNameAndPatronymic()
            throws UserNotFoundException {
        String firstName = "FirstName";
        String patronymic = "Patronymic";
        FullName fullName = new FullName(firstName, null, patronymic);

        List<UserEntity> expectedRepositoryOutput = new ArrayList<>();
        expectedRepositoryOutput.add(new UserEntity(1L, firstName, null, patronymic, new Date(0)));
        when(userRepo.findByFirstNameAndPatronymic(firstName, patronymic)).thenReturn(expectedRepositoryOutput);

        userService.getUsersByFullNameParts(fullName);
        verify(userRepo, times(1)).findByFirstNameAndPatronymic(firstName, patronymic);
    }

    @Test
    void getUsersByFullNameParts_shouldUseAllFullNameFieldsRepositoryMethod_whenInputHasAllFullNameFields()
            throws UserNotFoundException {
        String firstName = "FirstName";
        String lastName = "LastName";
        String patronymic = "Patronymic";
        FullName fullName = new FullName(firstName, lastName, patronymic);

        List<UserEntity> expectedRepositoryOutput = new ArrayList<>();
        expectedRepositoryOutput.add(new UserEntity(1L, firstName, lastName, patronymic, new Date(0)));
        when(userRepo.findByFirstNameAndLastNameAndPatronymic(firstName, lastName, patronymic))
                .thenReturn(expectedRepositoryOutput);

        userService.getUsersByFullNameParts(fullName);
        verify(userRepo, times(1))
                .findByFirstNameAndLastNameAndPatronymic(firstName, lastName, patronymic);
    }

    @Test
    void getUsersByDateOfBirthRange_shouldThrowException_whenDateFromAndDateToAreNull() {
        DateRange dateRange =  new DateRange();
        dateRange.setDateFrom(null);
        dateRange.setDateTo(null);

        assertThrows(IllegalArgumentException.class, () -> userService.getUsersByDateOfBirthRange(dateRange));
    }

    @Test
    void getUsersByDateOfBirthRange_shouldThrowException_whenNoSuchUserInDataBase() {
        Date dateFrom = new Date(2000);
        Date dateTo = new Date(2001);
        DateRange dateRange =  new DateRange();
        dateRange.setDateFrom(dateFrom);
        dateRange.setDateTo(dateTo);

        when(userRepo.findByDateOfBirthBetween(dateFrom, dateTo)).thenReturn(new ArrayList<>());

        assertThrows(UserNotFoundException.class, () -> userService.getUsersByDateOfBirthRange(dateRange));
    }

    @Test
    void getUsersByDateOfBirthRange_shouldUseBetweenMethod_whenBothDateRangeParametersAreNotNull() throws
            UserNotFoundException {
        Date dateFrom = new Date(2000);
        Date dateTo = new Date(2002);
        DateRange dateRange =  new DateRange();
        dateRange.setDateFrom(dateFrom);
        dateRange.setDateTo(dateTo);

        List<UserEntity> repositoryOutput = new ArrayList<>();
        repositoryOutput.add(new UserEntity(1L, "f", "l", "p", new Date(2001)));
        when(userRepo.findByDateOfBirthBetween(dateFrom, dateTo)).thenReturn(repositoryOutput);

        userService.getUsersByDateOfBirthRange(dateRange);
        verify(userRepo, times(1)).findByDateOfBirthBetween(dateFrom, dateTo);
    }

    @Test
    void getUsersByDateOfBirthRange_shouldUseSingleDateMethod_whenDateFromNotNullAndDateToIsNull() throws
            UserNotFoundException {
        Date dateFrom = new Date(2000);
        DateRange dateRange =  new DateRange();
        dateRange.setDateFrom(dateFrom);
        dateRange.setDateTo(null);

        List<UserEntity> repositoryOutput = new ArrayList<>();
        repositoryOutput.add(new UserEntity(1L, "f", "l", "p", new Date(2000)));
        when(userRepo.findByDateOfBirth(dateFrom)).thenReturn(repositoryOutput);

        userService.getUsersByDateOfBirthRange(dateRange);
        verify(userRepo, times(1)).findByDateOfBirth(dateFrom);
    }

    @Test
    void getUsersByDateOfBirthRange_shouldUseSingleDateMethod_whenDateToNotNullAndDateFromIsNull() throws
            UserNotFoundException {
        Date dateTo = new Date(2002);
        DateRange dateRange =  new DateRange();
        dateRange.setDateFrom(null);
        dateRange.setDateTo(dateTo);

        List<UserEntity> repositoryOutput = new ArrayList<>();
        repositoryOutput.add(new UserEntity(1L, "f", "l", "p", new Date(2002)));
        when(userRepo.findByDateOfBirth(dateTo)).thenReturn(repositoryOutput);

        userService.getUsersByDateOfBirthRange(dateRange);
        verify(userRepo, times(1)).findByDateOfBirth(dateTo);
    }

    @Test
    void updateUser_shouldThrowException_whenNoUserWithInputIdInDataBase() {
        Long id = 1L;
        User user = new User(id, "f", "l", "p",  new Date(2002));

        when(userRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(user));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser_whenInputHasFieldsToUpdate() throws UserNotFoundException {
        Long id = 1L;
        User user = new User(id, "f", null, "p",  new Date(2002));

        when(userRepo.findById(user.getId()))
                .thenReturn(Optional.of(
                        new UserEntity(id, "fff", "lll", "ppp",  new Date(2001))));

        User expected = new User(id, "f", "lll", "p",  new Date(2002));

        assertEquals(expected, userService.updateUser(user));
    }
}
