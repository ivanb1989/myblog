package it.cgmconsulting.myblog.payload.request;

import it.cgmconsulting.myblog.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostRequest{

	@NotBlank @Size(max=100)
	private String title;

	@NotBlank @Size(min= 20, max=3000)
	private String content;

	@NotBlank @Size(min= 20, max=255)
	private String overview;
}
