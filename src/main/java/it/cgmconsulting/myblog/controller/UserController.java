package it.cgmconsulting.myblog.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.cgmconsulting.myblog.entity.Authority;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.security.UserPrincipal;
import it.cgmconsulting.myblog.service.AuthorityService;
import it.cgmconsulting.myblog.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("user")//http:localhost:8080/user/...
@SecurityRequirement(name = "myBlogSecurityScheme")
@Validated
public class UserController{

	private final UserService userService;
	private final AuthorityService authorityService;
	private final PasswordEncoder passwordEncoder;


	public UserController(UserService userService, AuthorityService authorityService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.authorityService = authorityService;
		this.passwordEncoder = passwordEncoder;
	}

	@PutMapping("/change-role/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Transactional
	public ResponseEntity<?> changeRole(@PathVariable long id, @RequestParam @NotEmpty Set<String> newAuthorities, @AuthenticationPrincipal UserPrincipal userPrincipal) {
		if(userPrincipal.getId() == id)
			return new ResponseEntity("you cannot change your authorities", HttpStatus.FORBIDDEN);

		//recuperare l'utente in base all'id
		Optional<User> user = userService.findById(id);

		//verifico che chi vuole cambiare ruole non sia il richiedente stesso
		if(! user.isPresent()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
		//genero un Set di Authority partendo dal Set di stringhe di ruoli
		Set<Authority> authorities = authorityService.findByAuthorityNameIn(newAuthorities);
		if(authorities.isEmpty()) return new ResponseEntity<>("No authority selected", HttpStatus.NOT_FOUND);
		//settare i ruoli passti nella request
		user.get().setAuthorities(authorities);
		//salviamo

		return new ResponseEntity("Authorities updated for user " + user.get().getUsername(), HttpStatus.OK);
	}

	@PutMapping("/change-pwd")
	public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam @Pattern(regexp = "^[a-zA-Z0-9]{6,10}$", message = "Password must be of 6 to 10 length with no special characters") String newPassword) {

		Optional<User> user = userService.findById(userPrincipal.getId());
		if(! user.isPresent()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
		user.get().setPassword(passwordEncoder.encode(newPassword));
		return new ResponseEntity<>("Password changed", HttpStatus.OK);
	}

	@Transactional
	//metodo alternativo al cambio pwd
	@PutMapping("/change-pwd2")
	public ResponseEntity<?> changePassword2(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam @Pattern(regexp = "^[a-zA-Z0-9]{6,10}$", message = "Password must be of 6 to 10 length with no special characters") String newPassword) {

		userService.updatePassword(userPrincipal.getId(), passwordEncoder.encode(newPassword), LocalDateTime.now());
		return new ResponseEntity<>("Password changed", HttpStatus.OK);
	}

	@PutMapping("change-username")
	@Transactional
	public ResponseEntity<?> changeUsername(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam @NotBlank @Size(max = 20, min = 6) String newUsername) {

		if(! userService.existsByUsernameAndIdNot(newUsername, userPrincipal.getId())) {
			userService.updateUsername(userPrincipal.getId(), newUsername, LocalDateTime.now());
			return new ResponseEntity<>("Username changed", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Username already exists", HttpStatus.NOT_ACCEPTABLE);
		}

	}
}
