package employmentalert.application.notification;

import employmentalert.application.crawler.JobKoreaService;
import employmentalert.domain.jobPosting.JobPosting;
import employmentalert.domain.notification.NotificationChannel;
import employmentalert.domain.notification.NotificationHistory;
import employmentalert.domain.notification.NotificationRecord;
import employmentalert.domain.notification.repository.NotificationHistoryQueryRepository;
import employmentalert.domain.notification.repository.NotificationHistoryRepository;
import employmentalert.domain.notification.repository.NotificationRecordQueryRepository;
import employmentalert.domain.notification.repository.NotificationRecordRepository;
import employmentalert.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationHistoryRepository notificationHistoryRepository;
    private final NotificationRecordRepository notificationRecordRepository;
    private final NotificationHistoryQueryRepository notificationHistoryQueryRepository;
    private final NotificationRecordQueryRepository notificationRecordQueryRepository;
    private final JobKoreaService jobKoreaService;

    /**
     * 알림 발송 성공 이력 저장
     */
    @Transactional
    public NotificationHistory success(String userEmail, String subject, String content, NotificationChannel notificationChannel) {
        return notificationHistoryRepository.save(
                NotificationHistory.success(
                        userEmail,
                        subject,
                        content,
                        notificationChannel
                )
        );
    }

    /**
     * 알림 발송 실패 이력 저장
     */
    @Transactional
    public void fail(String userEmail, String subject, String content, NotificationChannel notificationChannel, String errorMessage) {
        notificationHistoryRepository.save(
                NotificationHistory.fail(
                        userEmail,
                        subject,
                        content,
                        notificationChannel,
                        errorMessage
                )
        );
    }

    /**
     * 알림 발송 이력 매핑 (유저 ↔ 공고 ↔ 알림이력)
     */
    @Transactional
    public void createRecord(User user, JobPosting jobPosting, NotificationHistory notificationHistory) {
        notificationRecordRepository.save(
                NotificationRecord.create(
                        user,
                        jobPosting,
                        notificationHistory
                )
        );
    }

    /**
     * 기준일 이전의 알림 매핑 정보, 히스토리, 채용공고 삭제
     */
    @Transactional
    public void cleanUpExpiredNotifications(LocalDateTime localDateTime) {
        // 기준일 이전 알림 히스토리 조회
        List<Long> expiredSuccessHistoryIds = notificationHistoryQueryRepository.findExpiredHistoryIds(localDateTime);

        if (expiredSuccessHistoryIds.isEmpty()) return;

        // 위 히스토리에 연관된 공고 조회
        List<Long> jobPostingIds = jobKoreaService.findJobPostingIdsByNotificationHistoryIds(expiredSuccessHistoryIds);

        // 순서대로 삭제 처리
        notificationRecordQueryRepository.deleteNotificationRecordsByHistoryIds(expiredSuccessHistoryIds);
        jobKoreaService.deleteJobPostings(jobPostingIds);
        notificationHistoryQueryRepository.deleteByIds(expiredSuccessHistoryIds);
    }
}
