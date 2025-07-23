package employmentalert.application.crawler;

import employmentalert.application.crawler.dto.JobKoreaPostingInfo;
import employmentalert.domain.jobPosting.JobPosting;
import employmentalert.domain.jobPosting.repository.JobPostingQueryRepository;
import employmentalert.domain.jobPosting.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobKoreaService {

    private final JobPostingRepository jobPostingRepository;
    private final JobPostingQueryRepository jobPostingQueryRepository;

    /**
     * 중복되지 않은 채용공고 저장
     */
    @Transactional
    public void saveAll(List<JobKoreaPostingInfo> jobKoreaPostingInfos) {
        Set<String> urls = jobPostingQueryRepository.existingUrls(
                jobKoreaPostingInfos.stream()
                        .map(JobKoreaPostingInfo::getUrl)
                        .toList()
        );

        List<JobKoreaPostingInfo> jobPostings = jobKoreaPostingInfos.stream()
                .filter(jobKoreaPostingInfo -> !urls.contains(jobKoreaPostingInfo.getUrl()))
                .toList();

        log.info("중복된 공고 수 : {}", jobKoreaPostingInfos.size() - jobPostings.size());

        List<JobPosting> createJobPostings = jobPostingRepository.saveAll(
                jobPostings.stream()
                        .map(jobKoreaPostingInfo -> JobPosting.create(
                                jobKoreaPostingInfo.getCompany(),
                                jobKoreaPostingInfo.getTitle(),
                                jobKoreaPostingInfo.getUrl(),
                                jobKoreaPostingInfo.getCareer(),
                                jobKoreaPostingInfo.getEducation(),
                                jobKoreaPostingInfo.getEmploymentType(),
                                jobKoreaPostingInfo.getRegion(),
                                jobKoreaPostingInfo.getDeadline()
                        ))
                        .toList()
        );

        log.info("저장된 공고 수 : {}", createJobPostings.size());
    }

    /**
     * 공고 삭제
     */
    @Transactional
    public void deleteJobPostings(List<Long> jobPostingIds) {
        jobPostingQueryRepository.deleteByIds(jobPostingIds);
    }

    /**
     * 알림 발송 이력이 있는 공고 조회
     */
    public List<Long> findJobPostingIdsByNotificationHistoryIds(List<Long> historyIds) {
        return jobPostingQueryRepository.findJobPostingIdsByNotificationHistoryIds(historyIds);
    }
}
