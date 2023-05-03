package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.entity.Authority;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.mail.MailService;
import it.cgmconsulting.myblog.payload.request.SignInRequest;
import it.cgmconsulting.myblog.payload.request.SignUpRequest;
import it.cgmconsulting.myblog.payload.response.JwtAuthenticationResponse;
import it.cgmconsulting.myblog.security.JwtTokenProvider;
import it.cgmconsulting.myblog.security.UserPrincipal;
import it.cgmconsulting.myblog.service.AuthorityService;
import it.cgmconsulting.myblog.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("auth")
public class AuthController{

	@Value("${app.mail.sender}")
	private String mailFrom;

	private final UserService userService;
	private final AuthorityService authorityService;
	private final MailService mailService;
	private final  AuthenticationManager authenticationManager;


	public AuthController(UserService userService, AuthorityService authorityService, MailService mailService, MailService mailService1, AuthenticationManager authenticationManager) {
		this.userService = userService;
		this.authorityService = authorityService;
		this.mailService = mailService1;
		this.authenticationManager=authenticationManager;


	}

	@PostMapping("/signup")//localhost:8080/auth/signup
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest request) {
		//istanziare un oggetto user e verifico che non esista nesusn altro utente con uusername o email passati

		if(userService.existsByUsernameOrEmail(request.getUsername(), request.getEmail()))
			return new ResponseEntity<>("user or email already in use", HttpStatus.valueOf(422));

		//Istanzio un oggetto user
		User u = userService.fromSignUpRequestToEntity(request);
		Optional<Authority> a = authorityService.findByAuthorityName("ROLE_READER");
		//associargli un ruolo
		if(! a.isPresent())
			return new ResponseEntity<>("Something went wrong role assignment", HttpStatus.UNPROCESSABLE_ENTITY);
		u.getAuthorities().add(a.get());
		u.setEnabled(true);
		//u.setConfirmCode(UUID.randomUUID().toString());
		userService.save(u);

		/*Mail m = new Mail(mailFrom, u.getEmail(), "Myblog: conferma mail", "Clicca qui per confermare la tua registrazione qui http://localhost:8080/auth/confirm" + u.getConfirmCode());
		mailService.sendMail(m);*/
		return new ResponseEntity<>(" An email hai sent to you. PLEASE CONFIRM YOUR REGISTRATION", HttpStatus.CREATED);
	}

	@PutMapping("/confirm{confirmCode}")
	@Transactional
	public ResponseEntity<?> confirmRegistration(@PathVariable String confirmCode) {
		//trovare utente per confirmCode
		Optional<User> u = userService.findByConfirmCode(confirmCode);
		if(! u.isPresent()) return new ResponseEntity<>("you account has been activated", HttpStatus.BAD_REQUEST);
		//abilitarlo
		u.get().setEnabled(true);
		//settare a null il confirmCode
		u.get().setConfirmCode(null);
		u.get().getAuthorities().clear();
		u.get().getAuthorities().add(authorityService.findByAuthorityName("ROLE_READER").get());

		return new ResponseEntity("Welcome to out Blog", HttpStatus.OK);

	}

	@PostMapping("/signin")
	@Transactional
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInRequest request) {
		Optional<User> u = userService.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail());
		if(! u.isPresent()) return new ResponseEntity<String>("Bad Credentials", HttpStatus.FORBIDDEN);


		if(!userService.checkPassword(request.getPassword(), u.get().getPassword()))
			return new ResponseEntity<String>("Bad Credentials", HttpStatus.FORBIDDEN);

		//check per eventuale scandenza del ban
		if(u.get().isEnabled()){
			if(u.get().getEndBanOn()==null)
				return new ResponseEntity<String>("Please confim tour registration",HttpStatus.FORBIDDEN);
			else{
				if(u.get().getEndBanOn().isAfter(LocalDate.now()))
					return new ResponseEntity<String>("You are banned until "+u.get().getEndBanOn(),HttpStatus.FORBIDDEN);
				else
					u.get().setEnabled(true);
			}
		}


			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getUsernameOrEmail(),
						request.getPassword()
				)
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = JwtTokenProvider.generateToken(authentication);
		JwtAuthenticationResponse currentUser = UserPrincipal.createJwtAuthenticationResponseFromUserPrincipal((UserPrincipal) authentication.getPrincipal(), jwt);

		return ResponseEntity.ok(currentUser);
}
}