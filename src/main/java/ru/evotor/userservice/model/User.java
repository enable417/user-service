package ru.evotor.userservice.model;

import ru.evotor.userservice.entity.UserEntity;

import java.util.Date;
import java.util.Objects;

public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Date dateOfBirth;

    public static User toModel(UserEntity entity) {
        User model = new User();
        model.setId(entity.getId());
        model.setFirstName(entity.getFirstName());
        model.setLastName(entity.getLastName());
        model.setPatronymic(entity.getPatronymic());
        model.setDateOfBirth(entity.getDateOfBirth());
        return model;
    }

    public static UserEntity toEntity(User model) {
        UserEntity entity = new UserEntity();
        entity.setFirstName(model.getFirstName());
        entity.setLastName(model.getLastName());
        entity.setPatronymic(model.getPatronymic());
        entity.setDateOfBirth(model.getDateOfBirth());
        return entity;
    }

    public User() {
    }

    public User(Long id, String firstName, String lastName, String patronymic, Date dateOfBirth) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.dateOfBirth = dateOfBirth;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", patronymic='").append(patronymic).append('\'');
        sb.append(", dateOfBirth=").append(dateOfBirth);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) &&
                firstName.equals(user.firstName) &&
                lastName.equals(user.lastName) &&
                patronymic.equals(user.patronymic) &&
                dateOfBirth.equals(user.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, patronymic, dateOfBirth);
    }
}
