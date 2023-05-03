package it.cgmconsulting.myblog.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.service.PdfService;
import it.cgmconsulting.myblog.service.PostService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.Optional;

@RestController
@RequestMapping("/pdf")
@SecurityRequirement(name = "myBlogSecurityScheme")
public class PdfController{

	private final PostService postService;
	private final PdfService pdfService;

	public PdfController(PostService postService, PdfService pdfService) {
		this.postService = postService;
		this.pdfService = pdfService;
	}


	@GetMapping("/public/{id}")
	public ResponseEntity<?> createPdf(@PathVariable long id) {

		Optional<Post> p = postService.findByIdAndPublishedTrue(id);
		if(! p.isPresent()) return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);

		InputStream pdfFile = null;
		ResponseEntity<InputStreamResource> responseEntity = null;
		try {
			pdfFile = pdfService.createPdf(p.get());
			HttpHeaders headers= new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType("application/pdf"));
			headers.add("Access-Control-Allow-Origin", "*");//da qualunque ip arrivi la chiamata è permessa
			headers.add("Access-Control-Allow-Method", "GET");//metodo utilizzato
			headers.add("Access-Control-Allow-Header", "Content-Type");//
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");//no cache
			headers.add("Pragma", "no-cache");//no cache
			headers.add("Expires", "0");//no cache
			headers.add("Content-disposition", "inline; filename=" + p.get().getId() + ".pdf");//in download assegna il nome

			responseEntity = new ResponseEntity<InputStreamResource>(new InputStreamResource(pdfFile),
					headers,
					HttpStatus.OK);
		} catch(Exception e) {
			responseEntity = new ResponseEntity<InputStreamResource>(
					new InputStreamResource(null,"Error creating pdf"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
    return responseEntity;

	}
}

