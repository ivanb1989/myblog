package it.cgmconsulting.myblog.payload.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class PostBoxesResponse{

	private long id;
	private String title;
	private String overview;
	private String image;
}
