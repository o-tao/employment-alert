package employmentalert.domain.notification.repository;

import employmentalert.domain.notification.NotificationChannel;
import employmentalert.domain.notification.NotificationHistory;
import employmentalert.global.config.QueryDslConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({NotificationHistoryQueryRepository.class, QueryDslConfig.class})
class NotificationHistoryQueryRepositoryTest {

    @Autowired
    private NotificationHistoryQueryRepository notificationHistoryQueryRepository;
    @Autowired
    private NotificationHistoryRepository notificationHistoryRepository;

    @AfterEach
    void clean() {
        notificationHistoryRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("기준 시간 이전에 생성된 알림 이력이 존재할 때, 해당 기준 시간으로 조회하면 그 이력만 조회된다.")
    public void findExpiredHistoryIdsTest() {
        // given
        NotificationHistory old = notificationHistoryRepository.save(
                NotificationHistory.success("test.com", "subject", "content", NotificationChannel.EMAIL)
        );

        LocalDateTime now = LocalDateTime.now();

        NotificationHistory recent = notificationHistoryRepository.save(
                NotificationHistory.success("test.com", "subject", "content", NotificationChannel.EMAIL)
        );

        // when
        List<Long> notificationHistory = notificationHistoryQueryRepository.findExpiredHistoryIds(now);

        // then
        assertThat(notificationHistory).containsExactly(old.getId());
        assertThat(notificationHistory).doesNotContain(recent.getId());
    }

    @Test
    @DisplayName("히스토리 ID 리스트가 있을 때, 전체 히스토리를 삭제하면 데이터베이스에서 해당 히스토리들이 모두 삭제된다.")
    void deleteByIdsTest() {
        // given
        NotificationHistory notificationHistory1 = notificationHistoryRepository.save(
                NotificationHistory.success("test1.com", "subject1", "content1", NotificationChannel.EMAIL)
        );
        NotificationHistory notificationHistory2 = notificationHistoryRepository.save(
                NotificationHistory.success("test2.com", "subject2", "content2", NotificationChannel.EMAIL)
        );


        // when
        notificationHistoryQueryRepository.deleteByIds(List.of(notificationHistory1.getId(), notificationHistory2.getId())
        );

        // then
        assertThat(notificationHistoryRepository.findAll()).isEmpty();
    }

}
