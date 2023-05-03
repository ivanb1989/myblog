package it.cgmconsulting.myblog.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.cgmconsulting.myblog.entity.Comment;
import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.payload.request.CommentRequest;
import it.cgmconsulting.myblog.security.UserPrincipal;
import it.cgmconsulting.myblog.service.CommentService;
import it.cgmconsulting.myblog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("comment") // http:localhost:8080/comment/...
@SecurityRequirement(name = "myBlogSecurityScheme")
@Validated
public class CommentController {

	private final CommentService commentService;
	private final PostService postService;
	public CommentController(CommentService commentService, PostService postService) {
		this.commentService = commentService;
		this.postService = postService;
	}

	@PostMapping
	@PreAuthorize("hasRole('ROLE_READER')")
	public ResponseEntity<?> save(@RequestBody @Valid CommentRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal){
		Optional<Post> p = postService.findByIdAndPublishedTrue(request.getPostId());
		if(!p.isPresent())
			return new ResponseEntity("Post not found", HttpStatus.NOT_FOUND);

		Comment c = new Comment(request.getComment(), p.get(), new User(userPrincipal.getId()));
		commentService.save(c);
		return new ResponseEntity("New comment added to post", HttpStatus.CREATED);
	}
}
