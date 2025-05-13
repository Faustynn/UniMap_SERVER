package org.main.unimapapi.controllers;

import org.main.unimapapi.services.TempUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/temp-user")
public class TempUserController {

    @Autowired
    private TempUserService tempUserService;

    @PostMapping("/login")
    public void login(@RequestBody TempUserRequest req) {
        System.out.println("!!!!!!!!!!PRISLALY MNE: " + req.idUser + " " + req.deleteInMinutes);
        tempUserService.addTempUser(req.idUser, req.deleteInMinutes);
    }

    @GetMapping("/exists/{id}")
    public boolean exists(@PathVariable Long id) {
        return tempUserService.exists(id);
    }

    public static class TempUserRequest {
        public Long idUser;
        public int deleteInMinutes;
    }
}
