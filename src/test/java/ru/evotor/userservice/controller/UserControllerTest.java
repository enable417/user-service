package ru.evotor.userservice.controller;

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import ru.evotor.userservice.exception.UserNotFoundException;
import ru.evotor.userservice.model.User;
import ru.evotor.userservice.service.UserService;
import ru.evotor.userservice.wrapper.DateRange;
import ru.evotor.userservice.wrapper.FullName;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    final String BASE_URL = "/user";

    @Test
    void getAllUsers_shouldReturnBadRequest_whenNoUsersInDataBase() throws Exception {
        when(userService.getAllUsers()).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get(BASE_URL)).andExpect(status().isBadRequest());
    }

    @Test
    void getAllUsers_shouldReturnUserListResponse_whenExistUsersInDataBase() throws Exception {
        when(userService.getAllUsers()).thenReturn(new ArrayList<>());

        mockMvc.perform(get(BASE_URL)).andExpect(status().isOk())
                .andExpect(content().string(containsString("[]")));
    }

    @Test
    void getUserById_shouldReturnBadRequest_whenUserNotExists() throws Exception {
        String URL = BASE_URL + "/find";
        String idParam = "1";
        Long id = Long.valueOf(idParam);
        when(userService.getUserById(id)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get(URL).param("id", idParam)).andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_shouldReturnUser_whenUserWithIdExistsInDataBase() throws Exception {
        String URL = BASE_URL + "/find";
        String idParam = "1";
        Long id = Long.valueOf(idParam);
        when(userService.getUserById(id))
                .thenReturn(new User(1L, "f", "l", "p", new Date(0)));

        String expected = "{" +
                "'id' : 1," +
                "'firstName':'f'," +
                "'lastName':'l'," +
                "'patronymic':'p'," +
                "'dateOfBirth':'1970-01-01T00:00:00.000+00:00'}";

        mockMvc.perform(get(URL).param("id", idParam))
                .andExpect(status().isOk()).andExpect(content().json(expected));
    }

    @Test
    void getUsersByFullNameParts_shouldReturnBadRequest_whenFullNameFieldsAreNull() throws Exception {
        String URL = BASE_URL + "/find/full-name";

        FullName fullName = new FullName();
        fullName.setFirstName(null);
        fullName.setLastName(null);
        fullName.setPatronymic(null);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = writer.writeValueAsString(fullName);

        when(userService.getUsersByFullNameParts(fullName)).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUsersByFullNameParts_shouldReturnBadRequest_whenNoSuchUserInDataBase() throws Exception {
        String URL = BASE_URL + "/find/full-name";

        FullName fullName = new FullName();
        fullName.setFirstName("f");
        fullName.setLastName("l");
        fullName.setPatronymic("p");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = writer.writeValueAsString(fullName);

        when(userService.getUsersByFullNameParts(fullName)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUsersByFullNameParts_shouldReturnListOfUsers_whenUserExistsInDataBase() throws Exception {
        String URL = BASE_URL + "/find/full-name";

        FullName fullName = new FullName();
        fullName.setFirstName("f");
        fullName.setLastName("l");
        fullName.setPatronymic("p");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = writer.writeValueAsString(fullName);

        when(userService.getUsersByFullNameParts(fullName)).thenReturn(new ArrayList<>());

        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    void getUsersByDateOfBirth_shouldReturnBadRequest_whenDateRangeFieldsAreNull() throws Exception {
        String URL = BASE_URL + "/find/date-of-birth";

        DateRange dateRange = new DateRange();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = writer.writeValueAsString(dateRange);

        when(userService.getUsersByDateOfBirthRange(dateRange)).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUsersByDateOfBirth_shouldReturnBadRequest_whenNoSuchUserInDataBase() throws Exception {
        String URL = BASE_URL + "/find/date-of-birth";

        DateRange dateRange = new DateRange();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = writer.writeValueAsString(dateRange);

        when(userService.getUsersByDateOfBirthRange(dateRange)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUsersByDateOfBirth_shouldReturnListOfUsers_whenUserExistsInDataBase() throws Exception {
        String URL = BASE_URL + "/find/date-of-birth";

        DateRange dateRange = new DateRange();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = writer.writeValueAsString(dateRange);

        when(userService.getUsersByDateOfBirthRange(dateRange)).thenReturn(new ArrayList<>());

        mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    void updateUser_shouldReturnBadRequest_whenNoSuchUserInDataBase() throws Exception {
        String URL = BASE_URL + "/update";

        User user = new User();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = writer.writeValueAsString(user);

        when(userService.updateUser(user)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_shouldReturnUpdatedUser_whenUserInDataBase() throws Exception {
        String URL = BASE_URL + "/update";

        User inputUser = new User(1L, "f", "l", "p", new Date(0));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = writer.writeValueAsString(inputUser);

        User updatedUser = new User(1L, "f", "l", "p", new Date(0));

        when(userService.updateUser(inputUser)).thenReturn(updatedUser);

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User successfully updated")));
    }
}
