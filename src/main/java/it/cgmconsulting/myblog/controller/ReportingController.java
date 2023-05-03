package it.cgmconsulting.myblog.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.cgmconsulting.myblog.entity.*;
import it.cgmconsulting.myblog.entity.common.ReportingStatus;
import it.cgmconsulting.myblog.payload.request.ReasonRemoveRequest;
import it.cgmconsulting.myblog.payload.request.ReasonRequest;
import it.cgmconsulting.myblog.payload.request.ReportingRequest;
import it.cgmconsulting.myblog.payload.response.ReportingResponse;
import it.cgmconsulting.myblog.security.UserPrincipal;
import it.cgmconsulting.myblog.service.CommentService;
import it.cgmconsulting.myblog.service.ReportingService;
import it.cgmconsulting.myblog.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("reporting") // http:localhost:8080/reporting/...
@SecurityRequirement(name = "myBlogSecurityScheme")
@Validated
public class ReportingController{

	private final CommentService commentService;
	private final ReportingService reportingService;
	private final UserService userService;

	public ReportingController(CommentService commentService, ReportingService reportingService, UserService userService) {
		this.commentService = commentService;
		this.reportingService = reportingService;
		this.userService = userService;
	}


	//AGGIUNGE SEGNALAZIONE
	@PostMapping
	@PreAuthorize("hasRole('ROLE_READER')")
	public ResponseEntity<?> createReporting(@RequestBody ReportingRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal) {

		Optional<Comment> c = commentService.findByIdAndCensoredFalse(request.getCommentId());
		if(! c.isPresent()) return new ResponseEntity("Comment not found", HttpStatus.NOT_FOUND);

		if(reportingService.existsById(new ReportingId(c.get())))
			return new ResponseEntity("This comment has been reported", HttpStatus.BAD_REQUEST);

		if(c.get().getAuthor().getId() == userPrincipal.getId())
			return new ResponseEntity("You cannot report your own comment", HttpStatus.BAD_REQUEST);

		Reason r = reportingService.getValidReasonByReasonId(request.getReasonId(), LocalDate.now());
		if(r == null) return new ResponseEntity("No valid reason found", HttpStatus.NOT_FOUND);

		Reporting rep = new Reporting(new ReportingId(c.get()), new User(userPrincipal.getId()), r);
		reportingService.save(rep);

		return new ResponseEntity("Comment " + c.get().getId() + " has been reported", HttpStatus.CREATED);

	}

	//STATO SEGNALAZIONI PER TIPO
	@GetMapping("/{status}")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
	public ResponseEntity<?> getReportings(@PathVariable @NotNull ReportingStatus status) {
		List<ReportingResponse> reportings = reportingService.getReportings(status);
		return new ResponseEntity(reportings, HttpStatus.OK);
	}


	@PatchMapping("/{commentId}/{status}")
	@PreAuthorize("hasRole('ROLE_MODERATOR')")
	@Transactional
	public ResponseEntity<?> updateReporting(@PathVariable ReportingStatus status, @PathVariable long commentId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        /*
        OPEN -> IN_PROGRESS -> cambiamo lo stato e assegniamo la segnalazione a un moderatore

        IN_PROGRESS -> CLOSED_WITH_BAN, CLOSED_WITHOUT_BAN, PERMABAN ->
           ->> CLOSED_WITHOUT_BAN: cambio solo lo stato della segnalazione
           ->> CLOSED_WITH_BAN / PERMABAN: cambio lo stato della segnalazione; censuro il commento; disabilito lo user

        UNA VOLTA CHIUSA LA SEGNALAZIONE NON PUO' ESSERE RIAPERTA
         */

		Optional<Reporting> reporting = reportingService.findByReportingIdCommentId(commentId);

		if(! reporting.get().getStatus().equals(ReportingStatus.OPEN) && ! reporting.get().getStatus().equals(ReportingStatus.IN_PROGRESS))
			return new ResponseEntity<>("Report already closed", HttpStatus.BAD_REQUEST);

		if((reporting.get().getModerator() != null && reporting.get().getModerator().getId() != userPrincipal.getId()))
			return new ResponseEntity<>("This reporting is in charge to another moderator", HttpStatus.BAD_REQUEST);

		String msg = "Status changed in " + status;
		HttpStatus httpStatus = HttpStatus.OK;

		switch(status) {
			case IN_PROGRESS: {
				if(! reporting.get().getStatus().equals(ReportingStatus.IN_PROGRESS)) {
					reporting.get().setStatus(ReportingStatus.IN_PROGRESS);
					reporting.get().setModerator(new User(userPrincipal.getId()));
				} else {
					msg = "Illegal Status " + status;
					httpStatus = HttpStatus.BAD_REQUEST;
				}
				break;
			}
			case CLOSED_WITHOUT_BAN: {
				reporting.get().setStatus(ReportingStatus.CLOSED_WITHOUT_BAN);
				break;
			}
			case CLOSED_WITH_BAN, PERMABAN: {
				if(! reporting.get().getStatus().equals(ReportingStatus.OPEN)) {
					if(status.equals(ReportingStatus.CLOSED_WITH_BAN))
						reporting.get().setStatus(ReportingStatus.CLOSED_WITH_BAN);
					else reporting.get().setStatus(ReportingStatus.PERMABAN);
					Optional<Comment> c = commentService.findByIdAndCensoredFalse(commentId);
					c.get().setCensored(true);
					c.get().getAuthor().setEnabled(false);
				} else {
					msg = "Illegal update from OPEN to BAN";
					httpStatus = HttpStatus.BAD_REQUEST;
				}
				break;
			}
			default:
				return new ResponseEntity<>("Illegal status", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(msg, httpStatus);
	}

	@PatchMapping("reassign/{from}/{to}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> reassignReporting(@PathVariable long from, @PathVariable long to, @RequestParam @NotEmpty Set<Long> ids){
		// Verifico che il nuovo assegnatario esista e sia MODERATOR
		if(userService.getModeratorsToReassignement(to) == 1) {
			// QUERY DI UPDATE E AGGIORNO I RECORD (ids) con il nuovo moderatore
			reportingService.reassignReportings(from, to, ReportingStatus.IN_PROGRESS, LocalDateTime.now(),ids);
			return new ResponseEntity<>("Reportings has been reassigned to "+to, HttpStatus.OK);
		}
		return new ResponseEntity<>("Moderator not found", HttpStatus.BAD_REQUEST);
}

	/* ********************************** REASON ***************************************/

	//AGGIUNGE MOTIVAZIONE
	@PostMapping("add-reason") // ESCLUSIVAMENTE INSERT
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> addReason(@RequestBody @Valid ReasonRequest request) {
		// Verifica che non esista già la reason
		if(reportingService.existsById(request.getReasonId()))
			return new ResponseEntity("Reason already present", HttpStatus.BAD_REQUEST);
		// Istanziare un oggetto Reason e salvarlo
		Reason r = new Reason(request.getReasonId());
		reportingService.save(r);
		// Istanziare un oggetto ReasonHistory e salvarlo
		reportingService.save(new ReasonHistory(new ReasonHistoryId(r, request.getStartDate()), request.getSeverity()));
		return new ResponseEntity("New reason added", HttpStatus.CREATED);
	}

	//AGGIORNA MOTIVAZIONE
	@PutMapping("update-reason")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Transactional
	public ResponseEntity<?> updateReason(@RequestBody @Valid ReasonRequest request) {

		Optional<ReasonHistory> rh = reportingService.findFirstByReasonHistoryIdReasonIdOrderByReasonHistoryIdStartDateDesc(request.getReasonId());
		if(! rh.isPresent()) return new ResponseEntity("No reason found", HttpStatus.NOT_FOUND);

		if(rh.get().getSeverity() == request.getSeverity())
			return new ResponseEntity("You are setting the same severity", HttpStatus.BAD_REQUEST);

		if(rh.get().getReasonHistoryId().getStartDate().isAfter(request.getStartDate()))
			return new ResponseEntity("You are setting a wrong start date", HttpStatus.BAD_REQUEST);

		// Caso 1: modifica di reason in corso di validità, ovvero con endDate == null
		if(rh.get().getEndDate() == null) {
			rh.get().setEndDate(request.getStartDate());
		}
		// Caso 2: modifica di reason annullata, ovvero con endDate valorizzata
		ReasonHistory rhNew = new ReasonHistory(new ReasonHistoryId(rh.get().getReasonHistoryId().getReason(), request.getStartDate().plus(1, ChronoUnit.DAYS)), request.getSeverity());
		reportingService.save(rhNew);

		return new ResponseEntity("Reason " + request.getReasonId() + " has been updated", HttpStatus.OK);

	}

	//RIMUOVE MOTIVAZIONE(LOGIC)
	@PatchMapping("remove-reason") // delete logica
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Transactional
	public ResponseEntity<?> removeReason(@RequestBody @Valid ReasonRemoveRequest request) {

		Optional<ReasonHistory> rh = reportingService.findFirstByReasonHistoryIdReasonIdOrderByReasonHistoryIdStartDateDesc(request.getReasonId());
		if(rh.isPresent() && rh.get().getEndDate() == null) {
			rh.get().setEndDate(request.getEndDate());
			return new ResponseEntity("Reason " + request.getReasonId() + " has been removed", HttpStatus.OK);
		}

		return new ResponseEntity("Reason " + request.getReasonId() + " not found", HttpStatus.NOT_FOUND);
	}

	//LISTA MOTIVAZIONI
	@GetMapping("get-valid-reasons")
	@PreAuthorize("hasRole('ROLE_READER')")
	public ResponseEntity<?> getValidReason() {
		List<String> reasons = reportingService.getValidReasons();
		return new ResponseEntity(reasons, HttpStatus.OK);
	}

}
