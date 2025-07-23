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
     * 알림 발송 히스토리가 존재하지 않거나, 발송에 실패한 공고 조회
     * - 알림이력 - 유저 이메일 매칭
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
     * 알림 발송이력 있는 공고 조회
     */
    public List<Long> findJobPostingIdsByNotificationHistoryIds(List<Long> historyIds) {
        return jpaQueryFactory
                .select(notificationRecord.jobPosting.id)
                .from(notificationRecord)
                .where(notificationRecord.notificationHistory.id.in(historyIds))
                .distinct()
                .fetch();
    }

    /**
     * URL 중복 체크
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

    /**
     * 공고 삭제
     */
    public void deleteByIds(List<Long> ids) {
        jpaQueryFactory
                .delete(jobPosting)
                .where(jobPosting.id.in(ids))
                .execute();
    }
}
