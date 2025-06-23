package employmentalert.api.crawler.service;

import employmentalert.api.crawler.service.dto.JobPostingInfo;
import employmentalert.domain.jobPosting.JobPosting;
import employmentalert.domain.jobPosting.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;

    public void saveAll(List<JobPostingInfo> jobPostingInfos) {
        jobPostingRepository.saveAll(
                jobPostingInfos.stream()
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
    }
}
