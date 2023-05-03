package it.cgmconsulting.myblog.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ReasonHistory{

	@EmbeddedId
	@EqualsAndHashCode.Include
	private ReasonHistoryId reasonHistoryId;

	private LocalDate endDate;

	private int severity;

	public ReasonHistory(ReasonHistoryId reasonHistoryId, int severity) {
		this.reasonHistoryId = reasonHistoryId;
		this.severity = severity;
	}


/*


parolacce 2020-01-01 2022-12-31 5
parolacce 2023-01-01 null 3
*
*
*
*
* */


}
