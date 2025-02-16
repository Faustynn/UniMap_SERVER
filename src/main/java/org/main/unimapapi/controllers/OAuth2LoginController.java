package org.main.unimapapi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
}