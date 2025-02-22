package org.main.unimapapi.services;

import org.main.unimapapi.entities.Filter;
import org.main.unimapapi.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Service
public class FilterService {
    private Filter filter;

    //EXAMPLE FUNCTION
    public List<User> userLookup(){
        List<Predicate<User>> conditions = new ArrayList<>();
        conditions.add(User::isAdmin); //EXAMPLE
        conditions.add(user -> user.getUsername().contains("Bob"));//EXAMPLE
        //Как эта фигня работать должна, в conditions добавляете все что должно быть правдиво для того что вы ищете,
        //оттуда оно вам вернет список  елементов (на данный момент только юзеров) которые подходят под все критерии
        //Пример выше ищет всех админов у которых ник содержит "Bob"
        return filter.findUserByFilter(conditions);
    }

    @Autowired
    public FilterService(UserService userService){
        filter = new Filter(userService);
    }
}
