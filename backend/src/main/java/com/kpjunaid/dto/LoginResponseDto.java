package com.kpjunaid.dto;

import com.kpjunaid.annotation.ValidEmail;
import com.kpjunaid.annotation.ValidPassword;
import com.kpjunaid.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {


    private User user;

    private String group;
    private int id;


    public LoginResponseDto(int id, User user, String studyGroup) {

        this.id = id;

        this.user = user;

        this.group = studyGroup;

    }



}
