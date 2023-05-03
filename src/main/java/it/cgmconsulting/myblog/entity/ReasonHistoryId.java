package it.cgmconsulting.myblog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
@Embeddable@Getter@Setter@EqualsAndHashCode@AllArgsConstructor@NoArgsConstructor
public class ReasonHistoryId implements Serializable{

	@ManyToOne
	@JoinColumn(name="reason_id",nullable = false)
	@EqualsAndHashCode.Include
	private Reason reason;

	@Column(nullable = false)
	@EqualsAndHashCode.Include
	private LocalDate startDate;
}
