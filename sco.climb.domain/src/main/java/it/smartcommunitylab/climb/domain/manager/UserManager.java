package it.smartcommunitylab.climb.domain.manager;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

@Service
public class UserManager {

    @Autowired
    private RepositoryManager repoManager;

    public void registrate(User user) {
        if (StringUtils.isBlank(user.getEmail())) {
            throw new IllegalArgumentException("email cannot be blank");
        }

        if (StringUtils.isBlank(user.getCf())) {
            throw new IllegalArgumentException("cf cannot be blank");
        }

        if (StringUtils.isBlank(user.getName())) {
            throw new IllegalArgumentException("name cannot be blank");
        }

        if (StringUtils.isBlank(user.getSurname())) {
            throw new IllegalArgumentException("surname cannot be blank");
        }

        boolean cfValid = Pattern.matches(
                "^(?:(?:[B-DF-HJ-NP-TV-Z]|[AEIOU])[AEIOU][AEIOUX]|[B-DF-HJ-NP-TV-Z]{2}[A-Z]){2}[\\dLMNP-V]{2}(?:[A-EHLMPR-T](?:[04LQ][1-9MNP-V]|[1256LMRS][\\dLMNP-V])|[DHPS][37PT][0L]|[ACELMRT][37PT][01LM])(?:[A-MZ][1-9MNP-V][\\dLMNP-V]{2}|[A-M][0L](?:[1-9MNP-V][\\dLMNP-V]|[0L][1-9MNP-V]))[A-Z]$",
                user.getCf());
        if (!cfValid) {
            throw new IllegalArgumentException("CF is not valid");
        }

        boolean emailValid = Pattern.matches(
                "^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$", user.getEmail());
        if (!emailValid) {
            throw new IllegalArgumentException("email is not valid");
        }

        User existent = repoManager.getUserByCf(user.getCf());
        if (existent != null) {
            throw new IllegalArgumentException("user with this CF already present");
        }

        existent = repoManager.getUserByEmail(user.getEmail());
        if (existent != null) {
            throw new IllegalArgumentException("user with this email already present");
        }

        User toSave = new User();
        toSave.setCf(user.getCf());
        toSave.setEmail(user.getEmail());
        toSave.setName(user.getName());
        toSave.setSurname(user.getSurname());

        repoManager.addUser(toSave);


    }

}
