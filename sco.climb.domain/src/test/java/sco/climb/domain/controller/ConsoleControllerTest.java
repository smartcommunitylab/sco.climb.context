package sco.climb.domain.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.domain.config.AppConfig;
import it.smartcommunitylab.climb.domain.config.SecurityConfig;
import it.smartcommunitylab.climb.domain.security.DataSetDetails;
import it.smartcommunitylab.climb.domain.security.DataSetInfo;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AppConfig.class, SecurityConfig.class})
@TestPropertySource(properties = {"db.name=ClimbDomainTest"})

// Annotations used to permit correct loading of web application context with security
@EnableWebSecurity
@ComponentScan(basePackages = "it.smartcommunitylab.climb.domain")
public class ConsoleControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RepositoryManager repositoryManager;

    @Autowired
    private MongoTemplate mongoTemplate;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
        mongoTemplate.getDb().dropDatabase();
    }

    @Test
    public void acceptTermsExistingUser() throws Exception {
        final String userEmail = "me@example.org";
        User user = new User();
        user.setEmail(userEmail);
        repositoryManager.addUser(user);

        DataSetInfo dataSetInfo = new DataSetInfo();
        dataSetInfo.setEmail(userEmail);
        DataSetDetails myUser = new DataSetDetails(dataSetInfo);
        mvc.perform(post("/console/user/accept-terms").with(user(myUser)));

        User checkUser = repositoryManager.getUserByEmail(userEmail);
        assertThat(checkUser.getTermUsage().isAcceptance(), is(true));
    }

    @Test
    public void acceptTermsOfNotExistentUser() throws Exception {
        final String userEmail = "dummie@example.org";

        DataSetInfo dataSetInfo = new DataSetInfo();
        dataSetInfo.setEmail(userEmail);
        DataSetDetails myUser = new DataSetDetails(dataSetInfo);
        mvc.perform(post("/console/user/accept-terms").with(user(myUser)));

        User checkUser = repositoryManager.getUserByEmail(userEmail);
        assertThat(checkUser, is(nullValue()));
    }
}
