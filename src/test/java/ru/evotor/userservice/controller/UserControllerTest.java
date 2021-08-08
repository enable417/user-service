package ru.evotor.userservice.controller;

import static org.hamcrest.Matchers.*;

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

import java.nio.charset.Charset;
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
}
