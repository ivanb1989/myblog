package it.cgmconsulting.myblog.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignUpRequest{

	@NotBlank
	@Size(max = 20, min = 6)
	private String username;

	//@NotBlank @Size(max = 10, min = 6) //defualt "123456"
	@Pattern( regexp = "^[a-zA-Z0-9]{6,10}$",
	message = "Password must be of 6 to 10 length with no special characters")
	private String password;

	@NotBlank
	@Email
	private String email;


}
