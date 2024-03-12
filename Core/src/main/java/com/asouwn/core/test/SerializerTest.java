package com.asouwn.core.test;

import com.asouwn.commonTest.User;
import com.asouwn.core.serializer.JdkSerializer;
import com.asouwn.core.serializer.Serializer;

import java.io.IOException;

public class SerializerTest {
    public static void main(String[] args) throws IOException {
        User user = User.builder().name("asouwn").build();

        final Serializer serializer = new JdkSerializer();
        User user1 = serializer.deserializer(serializer.serializer(user), User.class);
        System.out.println(user1.getName());
    }
}
