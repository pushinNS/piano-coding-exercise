package io.piano.demo.it;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthenticationIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testSuccessfulRegistration() throws Exception {
        mvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("username=test1&password=111"))
                .andDo(print())
                .andExpect(header().exists("Authorization"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        mvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("username=test1&password=111"));
        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("username=test1&password=111"))
                .andDo(print())
                .andExpect(header().exists("Authorization"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testBadCredentials() throws Exception {
        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("username=test1&password=111"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("apiError?error=Bad credentials"));
    }

    @Test
    public void testUserAlreadyExist() throws Exception {
        mvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("username=test1&password=111"));
        mvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("username=test1&password=111"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("apiError?error=User already exists"));
    }

    @WithMockUser("test")
    @Test
    public void authorizedUserShouldReceive200() throws Exception {
        mvc.perform(get("/auth"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void unauthorizedUserShouldBeRedirected() throws Exception {
        mvc.perform(get("/auth"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testRegisterPageIsAccessible() throws Exception {
        mvc.perform(get("/register"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testLoginPageIsAccessible() throws Exception {
        mvc.perform(get("/register"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void contextLoads() {

    }

}
