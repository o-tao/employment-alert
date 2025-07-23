package employmentalert.domain.notification.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static employmentalert.domain.notification.QNotificationRecord.notificationRecord;

@Repository
@RequiredArgsConstructor
public class NotificationRecordQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 알림 이력 매핑 삭제
     */
    public void deleteNotificationRecordsByHistoryIds(List<Long> historyIds) {
        jpaQueryFactory
                .delete(notificationRecord)
                .where(notificationRecord.notificationHistory.id.in(historyIds))
                .execute();
    }
}
