package com.asouwn.consumer;

import com.asouwn.commonTest.UserServer;
import com.asouwn.commonTest.User;
import com.asouwn.core.proxy.JdkProxyFactory;

public class Example {
    public static void main(String[] args) {

        UserServer userServer = JdkProxyFactory.getProxy(UserServer.class);

        User asouwn = User.builder().name("asouwn").build();
        User getUser = userServer.getUser(asouwn);
        if (getUser != null)
            System.out.println("get the user successfully "+ getUser.getName());
        else System.out.println("cannot get the user");
    }
}
