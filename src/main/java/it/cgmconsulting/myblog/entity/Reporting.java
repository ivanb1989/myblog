package it.cgmconsulting.myblog.entity;

import it.cgmconsulting.myblog.entity.common.CreationUpdate;
import it.cgmconsulting.myblog.entity.common.ReportingStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Reporting extends CreationUpdate{

	@EqualsAndHashCode.Include
	@EmbeddedId
	ReportingId reportingId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter", nullable = false)
	private User reporter;

	@ManyToOne
	@JoinColumn(name = "reason_id", nullable = false)
	private Reason reason;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false,length = 18)
	private ReportingStatus status= ReportingStatus.OPEN;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="moderator")
	private User moderator;

	public Reporting(ReportingId reportingId, User reporter, Reason reason) {
		this.reporter = reporter;
		this.reason = reason;
		this.reportingId=reportingId;
	}
}
