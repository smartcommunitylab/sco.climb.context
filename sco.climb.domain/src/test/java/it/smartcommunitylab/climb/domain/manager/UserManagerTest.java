package it.smartcommunitylab.climb.domain.manager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"db.name=ClimbDomainTest"})
public class UserManagerTest {

    @Autowired
    private UserManager userManager;

    @Autowired
    private RepositoryManager repoManager;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Rule
    public final ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void missing_email_in_registration() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage(is("email cannot be blank"));

        User user = new User();
        user.setCf("myCF");
        user.setName("name");
        user.setSurname("surname");

        userManager.registrate(user);
    }

    @Before
    public void setup() {
        mongoTemplate.getDb().dropDatabase();
    }

    @Test
    public void registration_OK() {

        User user = new User();
        user.setEmail("my@example.org");
        user.setCf("MLLSNT82P65Z404U");
        user.setName("name");
        user.setSurname("surname");

        userManager.registrate(user);
        
        User userFound = repoManager.getUserByEmail("my@example.org");
        assertThat(userFound, is(notNullValue()));
    }
}
