package employmentalert.application.notification;

import employmentalert.domain.jobPosting.JobPosting;
import employmentalert.domain.notification.NotificationChannel;
import employmentalert.domain.notification.NotificationHistory;
import employmentalert.domain.notification.NotificationRecord;
import employmentalert.domain.notification.repository.NotificationHistoryRepository;
import employmentalert.domain.notification.repository.NotificationRecordRepository;
import employmentalert.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationHistoryRepository notificationHistoryRepository;
    private final NotificationRecordRepository notificationRecordRepository;

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

    public void createRecord(User user, JobPosting jobPosting, NotificationHistory notificationHistory) {
        notificationRecordRepository.save(
                NotificationRecord.create(
                        user,
                        jobPosting,
                        notificationHistory
                )
        );
    }
}
