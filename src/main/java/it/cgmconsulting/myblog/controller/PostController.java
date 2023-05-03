package it.cgmconsulting.myblog.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.cgmconsulting.myblog.entity.Category;
import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.payload.request.CategoryRequest;
import it.cgmconsulting.myblog.payload.request.PostRequest;
import it.cgmconsulting.myblog.payload.response.PostDetailResponse;
import it.cgmconsulting.myblog.security.UserPrincipal;
import it.cgmconsulting.myblog.service.CategoryService;
import it.cgmconsulting.myblog.service.CommentService;
import it.cgmconsulting.myblog.service.PostService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("post")//http:localhost:8080/user/...
@SecurityRequirement(name = "myBlogSecurityScheme")
@Validated
public class PostController{

	@Value("${app.post.image.size}")
	private long postImageSize;

	@Value("${app.post.image.width}")
	private int postImageWidth;

	@Value("${app.post.image.height}")
	private int postImageHeight;

	@Value("${app.post.image.extensions}")
	private String[] postImageExtensions;

	private final PostService postService;
	private final CategoryService categoryService;
	private final CommentService commentService;

	public PostController(PostService postService, CategoryService categoryService, CommentService commentService) {
		this.postService = postService;
		this.categoryService = categoryService;
		this.commentService = commentService;
	}


	@PostMapping
	@PreAuthorize("hasRole('ROLE_WRITER')")
	public ResponseEntity<?> createPost(@RequestBody @Valid PostRequest postRequest, @AuthenticationPrincipal UserPrincipal userPrincipal) {

		if(postService.existsByTitle((postRequest.getTitle())))
			return new ResponseEntity<>("Post title already present", HttpStatus.BAD_REQUEST);
		Post p = postService.fromRequestToEntity(postRequest, new User(userPrincipal.getId()));
		postService.save(p);
		return new ResponseEntity<>("Post created", HttpStatus.CREATED);
	}


	@GetMapping("/public/boxes")
	public ResponseEntity<?> getBoxes() {
		return new ResponseEntity<>(postService.getPostBoxes(), HttpStatus.OK);
	}


	@PutMapping("/publication-flow{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Transactional
	public ResponseEntity<?> publicationFlow(@PathVariable long id) {

		Optional<Post> p = postService.findById(id);
		if(! p.isPresent()) return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
		p.get().setPublished(! p.get().isPublished());
		return new ResponseEntity<>("Post status has been updated", HttpStatus.OK);
	}


	@PatchMapping(value="/add-image/{id}", consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ROLE_WRITER')")
	@Transactional
	public ResponseEntity<?> addImage(@PathVariable long id, @AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam MultipartFile file){

		if(postService.checkSize(file, postImageSize))
			return new ResponseEntity("File size is too large", HttpStatus.BAD_REQUEST);
		if(!postService.checkDimension(postService.fromMultipartFileToBufferedImage(file), postImageHeight, postImageWidth))
			return new ResponseEntity("Wrong file dimensions", HttpStatus.BAD_REQUEST);
		if(postService.checkExtensions(file, postImageExtensions))
			return new ResponseEntity("File extension not allowed", HttpStatus.BAD_REQUEST);

		Optional<Post> p = postService.findById(id);

		if(!p.isPresent())
			return new ResponseEntity("Post not found", HttpStatus.NOT_FOUND);
		try {
			if(userPrincipal.getId() != p.get().getAuthor().getId())
				return new ResponseEntity("You are not the author of this post", HttpStatus.FORBIDDEN);
			String fileName = postService.uploadFile(file, id, p.get().getImage());
			p.get().setImage(fileName);
			if(fileName==null) System.out.println("***NULL***");
		} catch (Exception e){
			return new ResponseEntity("Something went wrong: image not uploaded", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity("Image uploaded", HttpStatus.OK);
	}


	@GetMapping("/public/{id}")
	public ResponseEntity<?> getPostDetail(@PathVariable long id) {
		PostDetailResponse p = postService.getByIdAndPublishedTrue(id);
		if(p != null)  {
			p.setCategories(postService.getCategoriesByPostId(id));
			p.setComments(commentService.getCommentsByPost(id));
			return new ResponseEntity(p, HttpStatus.OK);
		}
		return new ResponseEntity("Post not found", HttpStatus.NOT_FOUND);

	}
@Transactional
	@PreAuthorize("hasRole('ROLE_WRITER')")
	@PatchMapping("/{id}")
	public ResponseEntity<?> updatePost(@PathVariable long id, @AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Valid PostRequest postRequest) {

		//recupero il post

		Optional<Post> p = postService.findById(id);
		if(! p.isPresent()) return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);

		//verifica che l'autore del post sia lo stesso che lo vuole modificare

		if(p.get().getAuthor().getId() != userPrincipal.getId())
			return new ResponseEntity<>("Not authorized", HttpStatus.BAD_REQUEST);

		//check titolo non già presente
		if(postService.existsByTitleAndIdNot(postRequest.getTitle(), id))
			return new ResponseEntity<>("Post title already present", HttpStatus.BAD_REQUEST);

		//settare published= false
		p.get().setPublished(false);
		p.get().setTitle((postRequest.getTitle()));
		p.get().setContent(postRequest.getContent());
		p.get().setOverview(postRequest.getOverview());
		return new ResponseEntity("Post updated", HttpStatus.OK);
	}

	/************************************************ ASSOCIAZIONE CATEGOIE ************************************************************/

	@PreAuthorize("hasRole('ROLE_WRITER')")
	@PutMapping("/add-categories/{id}")
	@Transactional
	public ResponseEntity<?> addCategories(@PathVariable long id, @RequestBody @NotEmpty Set<CategoryRequest> categories) {
		Optional<Post> post = postService.findById(id);
		if(post.isEmpty()) {
			return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
		}
		Set<Category> cats = categoryService.findByCategoryNameInAndVisibleTrue(categories.stream().map(c -> c.getCategoryName()).collect(Collectors.toSet()));
		post.get().setCategories(cats);
		if(cats.isEmpty())
			return new ResponseEntity<>("Removed all categories from post", HttpStatus.OK);
		return new ResponseEntity("Categories added ti post", HttpStatus.OK);

	}

}
