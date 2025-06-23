package employmentalert.api.crawler.service;

import employmentalert.api.crawler.service.dto.JobPostingInfo;
import employmentalert.domain.jobPosting.JobPosting;
import employmentalert.domain.jobPosting.repository.JobPostingQueryRepository;
import employmentalert.domain.jobPosting.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final JobPostingQueryRepository jobPostingQueryRepository;

    public void saveAll(List<JobPostingInfo> jobPostingInfos) {
        Set<String> urls = jobPostingQueryRepository.existingUrls(
                jobPostingInfos.stream()
                        .map(JobPostingInfo::getUrl)
                        .toList()
        );

        List<JobPostingInfo> jobPostings = jobPostingInfos.stream()
                .filter(jobPostingInfo -> !urls.contains(jobPostingInfo.getUrl()))
                .toList();

        log.info("중복된 공고 수 : {}", jobPostingInfos.size() - jobPostings.size());

        List<JobPosting> createJobPostings = jobPostingRepository.saveAll(
                jobPostings.stream()
                        .map(jobPostingInfo -> JobPosting.create(
                                jobPostingInfo.getCompany(),
                                jobPostingInfo.getTitle(),
                                jobPostingInfo.getUrl(),
                                jobPostingInfo.getCareer(),
                                jobPostingInfo.getEducation(),
                                jobPostingInfo.getEmploymentType(),
                                jobPostingInfo.getRegion(),
                                jobPostingInfo.getDeadline()
                        ))
                        .toList()
        );

        log.info("저장된 공고 수 : {}", createJobPostings.size());
    }
}
