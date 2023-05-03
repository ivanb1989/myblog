package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Reason;
import it.cgmconsulting.myblog.entity.ReasonHistory;
import it.cgmconsulting.myblog.entity.Reporting;
import it.cgmconsulting.myblog.entity.ReportingId;
import it.cgmconsulting.myblog.entity.common.ReportingStatus;
import it.cgmconsulting.myblog.payload.response.ReportingResponse;
import it.cgmconsulting.myblog.repository.ReasonHistoryRepository;
import it.cgmconsulting.myblog.repository.ReasonRepository;
import it.cgmconsulting.myblog.repository.ReportingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ReportingService {

    private final ReportingRepository reportingRepository;
    private final ReasonRepository reasonRepository;
	private final ReasonHistoryRepository reasonHistoryRepository;

    public ReportingService(ReportingRepository reportingRepository, ReasonRepository reasonRepository, ReasonHistoryRepository reasonHistoryRepository) {
        this.reportingRepository = reportingRepository;
        this.reasonRepository = reasonRepository;
		this.reasonHistoryRepository = reasonHistoryRepository;
	}

	/* ********************************** REPORTING ************************************** */
	public void save(Reporting rep){
		reportingRepository.save(rep);
	}

	public boolean existsById(ReportingId reportingId){
		return reportingRepository.existsById(reportingId);
	}

	public List<ReportingResponse> getReportings(ReportingStatus status){
		return reportingRepository.getReportings(status);
	}

	public void reassignReportings(long from, long to, ReportingStatus status, LocalDateTime now, Set<Long> ids){
		reportingRepository.reassignReportings(from, to, status,now, ids);
}


	/* ********************************** REASON ************************************** */

	public Reason getValidReasonByReasonId(String reasonId, LocalDate now){
		return reasonRepository.getValidReasonByReasonId(reasonId, now);
	}

	public void save(Reason r){
		reasonRepository.save(r);
	}

	public boolean existsById(String reasonId){
		return reasonRepository.existsById(reasonId);
	}

	/* ********************************** REASON_HISTORY ************************************** */

	public Optional<ReasonHistory> findFirstByReasonHistoryIdReasonIdOrderByReasonHistoryIdStartDateDesc(String reasonId){
		return reasonHistoryRepository.findFirstByReasonHistoryIdReasonIdOrderByReasonHistoryIdStartDateDesc(reasonId);
	}

	public void save(ReasonHistory rh){
		reasonHistoryRepository.save(rh);
	}
	public List<String> getValidReasons(){
		return reasonHistoryRepository.getValidReasons();
	}


	public Optional<Reporting> findByReportingIdCommentId(long commentId) {
		return reportingRepository.findByReportingIdCommentId(commentId);
	}
}