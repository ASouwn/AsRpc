package com.asouwn.server;

import com.asouwn.commonTest.User;
import com.asouwn.commonTest.UserServer;

public class Provider implements UserServer {
    @Override
    public User getUser(User user) {
        return user;
    }
}
