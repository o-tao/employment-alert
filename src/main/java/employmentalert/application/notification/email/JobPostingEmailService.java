package employmentalert.application.notification.email;

import employmentalert.api.user.service.UserService;
import employmentalert.application.notification.NotificationService;
import employmentalert.application.notification.dto.EmailInfo;
import employmentalert.domain.jobPosting.JobPosting;
import employmentalert.domain.jobPosting.repository.JobPostingQueryRepository;
import employmentalert.domain.notification.NotificationChannel;
import employmentalert.domain.notification.NotificationHistory;
import employmentalert.domain.user.User;
import employmentalert.global.email.EmailService;
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
    private final EmailService emailService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final EmailTemplateService emailTemplateService;

    /**
     * 등록된 유저에게 채용공고 이메일 발송
     */
    @Transactional
    public void sendJobPostingEmails(List<Long> userIds) {
        for (Long userId : userIds) {
            User user = userService.getUserById(userId);
            sendIfHasNewPostings(user);
        }
    }

    /**
     * 유저별 공고 조회 및 이메일 발송
     */
    private void sendIfHasNewPostings(User user) {
        List<JobPosting> unsentJobPostings = jobPostingQueryRepository.findUnsentJobPostings(user.getEmail());
        if (unsentJobPostings.isEmpty()) return;

        String subject = "[📨채용공고] 새로운 공고 %d건이 도착했어요!".formatted(unsentJobPostings.size());
        String content = emailTemplateService.buildJobPostingEmailContent(unsentJobPostings);

        sendEmailAndRecord(user, unsentJobPostings, subject, content);
    }


    /**
     * 이메일 발송 및 발송 이력 저장
     */
    private void sendEmailAndRecord(User user, List<JobPosting> jobPostings, String subject, String content) {
        try {
            EmailInfo emailInfo = new EmailInfo(List.of(user.getEmail()), subject, content);
            emailService.sendEmail(emailInfo.toSend());

            NotificationHistory notificationHistory = notificationService.success(
                    user.getEmail(),
                    subject,
                    content,
                    NotificationChannel.EMAIL
            );

            for (JobPosting jobPosting : jobPostings) {
                notificationService.createRecord(user, jobPosting, notificationHistory);
            }

            log.info("이메일 발송 성공: {} to {}", subject, user.getEmail());

        } catch (Exception exception) {
            notificationService.fail(
                    user.getEmail(),
                    subject,
                    content,
                    NotificationChannel.EMAIL,
                    exception.getMessage()
            );

            log.error("이메일 발송 실패 recipientEmail: {}", user.getEmail(), exception);
        }
    }
}
