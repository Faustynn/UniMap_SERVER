package org.main.unimapapi.controllers;

import org.main.unimapapi.utils.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class BaseController {
    @Autowired
    protected JwtToken jwtToken;

    protected boolean isValidToken(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return !token.isEmpty() && jwtToken.validateAccessToken(token);
    }
}