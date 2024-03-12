package com.asouwn.core.serializer;

import java.io.*;

/**
 * 序列器
 * */

public class JdkSerializer implements Serializer{
    /**
     * 序列化
     * @param object
     * @param <T>
     * @return
     * @throws IOException
     * */
    @Override
    public <T> byte[] serializer(T object) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        outputStream.close();
        return outputStream.toByteArray();
    }
    /**
     * 反序列化
     * @param bytes
     * @param type
     * @param <T>
     * @return
     * @throws IOException
     * */
    @Override
    public <T> T deserializer(byte[] bytes, Class<T> type) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try{
            return (T) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            objectInputStream.close();
        }
    }
}
