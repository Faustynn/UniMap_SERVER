package org.main.unimapapi.entities;

import org.main.unimapapi.services.UserService;

import java.util.List;
import java.util.function.Predicate;

public class Filter {
    private UserService userService;

    public Filter(UserService userService) {
        this.userService = userService;
    }

    public List<User> findUserByFilter(List<Predicate<User>> conditions){
        List<User> baseList = userService.getAll();
        for(Predicate<User> cond: conditions){
            baseList.removeIf(cond.negate());
        }
        return baseList;
    }

}
