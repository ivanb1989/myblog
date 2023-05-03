package it.cgmconsulting.myblog.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.cgmconsulting.myblog.entity.common.ReportingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ReportingResponse {

	private long commentId;
	private String reporter; // username di colui che segnala
	private String commentAuthor; // username dell'autore del commento incriminato
	private String reasonId; // nome della motivazione
	private ReportingStatus status; // status della segnalazione
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	private LocalDateTime updatedAt;
}
