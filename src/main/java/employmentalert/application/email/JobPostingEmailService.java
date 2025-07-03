package employmentalert.application.email;

import employmentalert.application.email.dto.EmailInfo;
import employmentalert.domain.jobPosting.JobPosting;
import employmentalert.domain.jobPosting.repository.JobPostingQueryRepository;
import employmentalert.domain.notification.NotificationChannel;
import employmentalert.domain.notification.NotificationHistory;
import employmentalert.domain.notification.NotificationRecord;
import employmentalert.domain.notification.repository.NotificationHistoryRepository;
import employmentalert.domain.notification.repository.NotificationRecordRepository;
import employmentalert.domain.user.User;
import employmentalert.domain.user.repository.UserRepository;
import employmentalert.global.email.EmailService;
import employmentalert.global.exception.EmploymentAlertException;
import employmentalert.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPostingEmailService {

    private final JobPostingQueryRepository jobPostingQueryRepository;
    private final NotificationHistoryRepository notificationHistoryRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NotificationRecordRepository notificationRecordRepository;

    /**
     * 등록된 유저에게 채용공고 이메일 발송
     * - 알림 발송 History 저장
     * - 알림 매핑 테이블 저장
     */
    @Transactional
    public void sendJobPostingEmails(List<Long> userIds) {
        for (Long userId : userIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EmploymentAlertException(ErrorCode.USER_NOT_FOUND));

            List<JobPosting> unsentJobPostings = jobPostingQueryRepository.findUnsentJobPostings(user.getEmail());
            if (unsentJobPostings.isEmpty()) continue;

            // 템플릿 생성: 전체 공고 목록을 하나의 메일 내용으로 구성
            String subject = "[채용공고] 새로운 공고 %d건이 도착했어요!".formatted(unsentJobPostings.size());
            String content = buildEmailContent(unsentJobPostings);

            try {
                // 이메일 발송
                EmailInfo emailInfo = new EmailInfo(List.of(user.getEmail()), subject, content);
                emailService.sendEmail(emailInfo.toSend());

                NotificationHistory history = NotificationHistory.success(
                        user.getEmail(),
                        subject,
                        content,
                        NotificationChannel.EMAIL
                );
                notificationHistoryRepository.save(history);

                // 발송된 각 공고별로 이력 저장
                for (JobPosting jobPosting : unsentJobPostings) {
                    NotificationRecord notificationRecord = NotificationRecord.create(user, jobPosting, history);
                    notificationRecordRepository.save(notificationRecord);
                }

                log.info("이메일 발송 성공: {} to {}", subject, user.getEmail());

            } catch (Exception exception) {
                // 공고 하나씩 실패 이력 저장
                NotificationHistory history = NotificationHistory.fail(
                        user.getEmail(),
                        subject,
                        content,
                        NotificationChannel.EMAIL,
                        exception.getMessage()
                );
                notificationHistoryRepository.save(history);
                log.error("이메일 발송 실패 recipientEmail {}, 이유: {}", user.getEmail(), exception.getMessage(), exception);
            }
        }
    }

    /**
     * 임시로직
     */
    private String buildEmailContent(List<JobPosting> jobPostings) {
        StringBuilder builder = new StringBuilder();
        builder.append("📢 새로운 채용 공고가 등록되었습니다!\n\n");

        for (JobPosting job : jobPostings) {
            builder.append("""
                    🔹 회사명: %s \n
                    🔹 제목: %s \n
                    🔹 경력: %s \n
                    🔹 학력: %s \n
                    🔹 고용 형태: %s \n
                    🔹 지역: %s \n
                    🔹 마감일: %s \n
                    ▶ 상세보기: %s \n
                    \n
                    """.formatted(
                    job.getCompany(),
                    job.getTitle(),
                    job.getCareer(),
                    job.getEducation(),
                    job.getEmploymentType(),
                    job.getRegion(),
                    job.getDeadline(),
                    job.getUrl()
            ));
        }

        builder.append("좋은 기회가 되길 바랍니다!\n");
        return builder.toString();
    }
}
