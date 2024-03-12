package com.asouwn.commonTest;

public interface UserServer {
    /**
     * 返回姓名
     * @param user 你想得到名字的用户
     * @return name
     */
    User getUser(User user);
}
