package it.cgmconsulting.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostDetailResponse{


	private long id;
	private String title;
	private String content;
	private String image;
	private String username;
	private double avg;//voto medio
	Set<String> categories;
	List<CommentResponse> comments;

	public PostDetailResponse(long id, String title, String content, String image, String username, double avg) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.image = image;
		this.username = username;
		this.avg = avg;
	}
}
