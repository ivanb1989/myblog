package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Reporting;
import it.cgmconsulting.myblog.entity.ReportingId;
import it.cgmconsulting.myblog.entity.common.ReportingStatus;
import it.cgmconsulting.myblog.payload.response.ReportingResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ReportingRepository extends JpaRepository<Reporting, ReportingId>{

	@Query(value = "SELECT new it.cgmconsulting.myblog.payload.response.ReportingResponse(" +
			"rep.reportingId.comment.id, " +
			"rep.reporter.username, " +
			"rep.reportingId.comment.author.username, " +
			"rep.reason.id, " +
			"rep.status, " +
			"rep.updatedAt" +
			") FROM Reporting rep " +
			"WHERE rep.status = :status " +
			"ORDER BY rep.updatedAt DESC")
	List<ReportingResponse> getReportings(@Param("status") ReportingStatus status);


	Optional<Reporting> findByReportingIdCommentId(long commentId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE Reporting r SET r.moderator.id = :to " +
			"WHERE r.status = :status " +
			"AND r.updatedAt = :now " +
			"AND r.moderator.id = :from " +
			"AND r.reportingId.comment.id IN (:ids)")
	void reassignReportings(@Param("from") long from,
							@Param("to") long to,
							@Param("status") ReportingStatus status,
							@Param ("now") LocalDateTime now,
							@Param("ids") Set<Long> ids);
}