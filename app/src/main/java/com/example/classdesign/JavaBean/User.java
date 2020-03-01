package com.example.classdesign.JavaBean;

//和 用户信息表 中的字段相对应
public class User {
    private String phone;//手机号
    private String password;//密码

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }
}
