package employmentalert.domain.jobPosting.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import employmentalert.domain.jobPosting.JobPosting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static employmentalert.domain.jobPosting.QJobPosting.jobPosting;
import static employmentalert.domain.notification.QNotificationRecord.notificationRecord;

@Repository
@RequiredArgsConstructor
public class JobPostingQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 이메일 발송이력 없는 공고 조회
     */
    public List<JobPosting> findUnsentJobPostings(String recipientEmail) {
        return jpaQueryFactory
                .selectFrom(jobPosting)
                .where(
                        JPAExpressions
                                .selectOne()
                                .from(notificationRecord)
                                .where(
                                        notificationRecord.jobPosting.id.eq(jobPosting.id),
                                        notificationRecord.notificationHistory.recipientEmail.eq(recipientEmail),
                                        notificationRecord.notificationHistory.status.eq(true)
                                )
                                .notExists()
                )
                .fetch();
    }

    /**
     * 이메일 중복 체크
     */
    public Set<String> existingUrls(List<String> urls) {
        return urls.isEmpty()
                ? Set.of()
                : new HashSet<>(
                jpaQueryFactory
                        .select(jobPosting.url)
                        .from(jobPosting)
                        .where(jobPosting.url.in(urls))
                        .fetch()
        );
    }
}
