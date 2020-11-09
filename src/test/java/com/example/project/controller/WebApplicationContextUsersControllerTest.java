package com.example.project.controller;

import com.example.project.config.TestConfig;
import com.example.project.config.WebConfig;
import com.example.project.model.Role;
import com.example.project.model.User;
import com.example.project.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebConfig.class})
@WebAppConfiguration("src/webapp")
public class WebApplicationContextUsersControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private UserService userServiceMock;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        Mockito.reset(userServiceMock);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void whenAddUsersAndGetRequestAllUsersThenAllUsersAddedToModelAndUsersViewRendered() throws Exception {
        User userOne = new User("NameOne", "LastNameOne", (byte) 1, Role.VISITOR);
        userOne.setId(1L);
        User userTwo = new User("NameTwo", "LastNameTwo", (byte) 2, Role.VISITOR);
        userTwo.setId(2L);

        when(userServiceMock.listUsers()).thenReturn(Arrays.asList(userOne, userTwo));

        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attribute("users", hasSize(2)))
                .andExpect(model().attribute("users", hasItem(
                        allOf(
                                hasProperty("id", is(userOne.getId())),
                                hasProperty("name", is(userOne.getName())),
                                hasProperty("lastName", is(userOne.getLastName())),
                                hasProperty("age", is(userOne.getAge()))
                                )
                )))
                .andExpect(model().attribute("users", hasItem(
                        allOf(
                                hasProperty("id", is(userTwo.getId())),
                                hasProperty("name", is(userTwo.getName())),
                                hasProperty("lastName", is(userTwo.getLastName())),
                                hasProperty("age", is(userTwo.getAge()))
                        )
                )));
    }

    @Test
    public void whenGetRequestAddViewThenEmptyUserInModelAndAddViewRendered() throws Exception {
        mockMvc.perform(get("/users/add"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("add"))
                .andExpect(model().attribute("user", isA(User.class)))
                .andExpect(model().attribute("user", hasProperty("id")))
                .andExpect(model().attribute("user", hasProperty("name")))
                .andExpect(model().attribute("user", hasProperty("lastName")))
                .andExpect(model().attribute("user", hasProperty("age")));
    }

    @Test
    public void whenAddOneUserAndGetRequestEditUserThenUserAddedToModelAndEditViewRendered() throws Exception {
        User userOne = new User("NameOne", "LastNameOne", (byte) 1, Role.VISITOR);
        userOne.setId(1L);

        when(userServiceMock.getById(userOne.getId())).thenReturn(userOne);

        mockMvc.perform(get("/users/edit")
                .param("id", Long.toString(userOne.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attribute("user", is(userOne)))
                .andExpect(model().attribute("user", hasProperty("id", is(userOne.getId()))))
                .andExpect(model().attribute("user", hasProperty("name", is(userOne.getName()))))
                .andExpect(model().attribute("user", hasProperty("lastName", is(userOne.getLastName()))))
                .andExpect(model().attribute("user", hasProperty("age", is(userOne.getAge()))));
    }

    @Test
    public void whenAddOneUserAndGetRequestDeleteUserThenUserDeletedAndUsersViewRendered() throws Exception {
        User userOne = new User("NameOne", "LastNameOne", (byte) 1, Role.VISITOR);
        userOne.setId(1L);

        when(userServiceMock.getById(userOne.getId())).thenReturn(userOne);

        mockMvc.perform(get("/users/delete")
                .param("id", Long.toString(userOne.getId())))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users"))
                .andExpect(redirectedUrl("/users"))
                .andExpect(content().string(""));

        verify(userServiceMock, times(1)).getById(userOne.getId());
        verify(userServiceMock, times(1)).delete(userOne);
    }

    @Test
    public void whenPostRequestAddUserThenFormHasCorrectValuesAndModelHasProperAttributeAndServiceAddCalledOnceAndUsersViewRendered() throws Exception {
        User userOne = new User("NameOne", "LastNameOne", (byte) 1, Role.VISITOR);
        userOne.setId(1L);

        mockMvc.perform(post("/users/add")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "NameOne")
                .param("lastName", "LastNameOne")
                .param("age","1")
                .param("role", Role.VISITOR.toString()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users"))
                .andExpect(redirectedUrl("/users"))
                .andExpect(model().attribute("user", isA(User.class)))
                .andExpect(model().attribute("user", hasProperty("name", is(userOne.getName()))))
                .andExpect(model().attribute("user", hasProperty("lastName", is(userOne.getLastName()))))
                .andExpect(model().attribute("user", hasProperty("age", is(userOne.getAge()))))
                .andExpect(model().attribute("user", hasProperty("role", is(userOne.getRole()))));

        ArgumentCaptor<User> formObjectArgument = ArgumentCaptor.forClass(User.class);
        verify(userServiceMock, times(1)).add(formObjectArgument.capture());
        verifyNoMoreInteractions(userServiceMock);
        User formObject = formObjectArgument.getValue();
        assertThat(formObject.getName(), is(userOne.getName()));
        assertThat(formObject.getLastName(), is(userOne.getLastName()));
        assertThat(formObject.getAge(), is(userOne.getAge()));
        assertThat(formObject.getRole(), is(userOne.getRole()));
    }

    @Test
    public void whenPostRequestEditUserThenFormHasCorrectValuesAndServiceAddCalledOnceAndUsersViewRendered() throws Exception {
        User userOne = new User("NameOne", "LastNameOne", (byte) 1, Role.ADMIN);
        userOne.setId(1L);

        mockMvc.perform(post("/users/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "NameOne")
                .param("lastName", "LastNameOne")
                .param("age","1")
                .param("role", Role.ADMIN.toString())
                .param("weekDays", "NOT SELECTED YET"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users"))
                .andExpect(redirectedUrl("/users"));

        ArgumentCaptor<User> formObjectArgument = ArgumentCaptor.forClass(User.class);
        verify(userServiceMock, times(1)).edit(formObjectArgument.capture());
        verifyNoMoreInteractions(userServiceMock);
        User formObject = formObjectArgument.getValue();
        assertThat(formObject.getName(), is(userOne.getName()));
        assertThat(formObject.getLastName(), is(userOne.getLastName()));
        assertThat(formObject.getAge(), is(userOne.getAge()));
        assertThat(formObject.getRole(), is(userOne.getRole()));
    }
}
