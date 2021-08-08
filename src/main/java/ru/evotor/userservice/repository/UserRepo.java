package ru.evotor.userservice.repository;

import org.springframework.data.repository.CrudRepository;
import ru.evotor.userservice.entity.UserEntity;

import java.util.Date;
import java.util.List;

public interface UserRepo extends CrudRepository<UserEntity, Long> {
    List<UserEntity> findByLastName(String lastName);
    List<UserEntity> findByFirstName(String firstName);
    List<UserEntity> findByPatronymic(String firstName);
    List<UserEntity> findByFirstNameAndLastName(String firstName, String lastName);
    List<UserEntity> findByFirstNameAndPatronymic(String firstName, String patronymic);
    List<UserEntity> findByFirstNameAndLastNameAndPatronymic(String firstName, String lastName, String patronymic);
    List<UserEntity> findByDateOfBirthBetween(Date dateFrom, Date dateTo);
    List<UserEntity> findByDateOfBirth(Date dateOfBirth);

}
