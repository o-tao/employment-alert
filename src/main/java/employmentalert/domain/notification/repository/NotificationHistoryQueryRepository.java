package employmentalert.domain.notification.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static employmentalert.domain.notification.QNotificationHistory.notificationHistory;

@Repository
@RequiredArgsConstructor
public class NotificationHistoryQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * createdAt이 기준일보다 이전인 것에 대해 조회
     */
    public List<Long> findExpiredHistoryIds(LocalDateTime localDateTime) {
        return jpaQueryFactory
                .select(notificationHistory.id)
                .from(notificationHistory)
                .where(notificationHistory.createdAt.before(localDateTime))
                .fetch();
    }

    /**
     * 알림 이력 삭제
     */
    public void deleteByIds(List<Long> ids) {
        jpaQueryFactory
                .delete(notificationHistory)
                .where(notificationHistory.id.in(ids))
                .execute();
    }
}
