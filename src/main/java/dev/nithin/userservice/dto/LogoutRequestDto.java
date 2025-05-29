package dev.nithin.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LogoutRequestDto {
    private String token;
}
