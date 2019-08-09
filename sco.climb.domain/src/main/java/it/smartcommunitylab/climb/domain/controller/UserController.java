package it.smartcommunitylab.climb.domain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.domain.manager.MailManager;
import it.smartcommunitylab.climb.domain.manager.UserManager;

@RestController
public class UserController {

    @Autowired
    private UserManager userManager;
    
    @Autowired
    private MailManager mailManager;

    @PostMapping(value = "/public/api/registration")
    public void registration(@RequestBody User user) {
        userManager.registrate(user);
        mailManager.sendConsoleRegistration(user);
    }
}
