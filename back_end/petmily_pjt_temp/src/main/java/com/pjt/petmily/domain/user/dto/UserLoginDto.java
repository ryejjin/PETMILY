package com.pjt.petmily.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDto {

    private String userEmail;
    private String userPw;




}