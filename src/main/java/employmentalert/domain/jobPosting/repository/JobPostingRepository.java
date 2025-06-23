package employmentalert.domain.jobPosting.repository;

import employmentalert.domain.jobPosting.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
}
