package org.main.unimapapi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OAuth2LoginController {

    @GetMapping("/api/unimap_pc/authenticate/google")
    public String googleLogin() {
        return "redirect:/oauth2/authorization/google";
    }

    @GetMapping("/api/unimap_pc/authenticate/facebook")
    public String facebookLogin() {
        return "redirect:/oauth2/authorization/facebook";
    }

    @GetMapping("/login-success")
    @ResponseBody
    public String loginSuccess() {
        return "Successful login!\n You can back to the application.";
    }
}