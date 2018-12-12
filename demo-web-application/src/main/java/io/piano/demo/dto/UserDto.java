package io.piano.demo.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotBlank
    @Size(min = 3, max = 15)
    private String username;
    @NotBlank
    @Size(min = 3, max = 15)
    private String password;
}
