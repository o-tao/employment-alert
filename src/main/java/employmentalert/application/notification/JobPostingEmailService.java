package employmentalert.application.notification;

import employmentalert.api.user.service.UserService;
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
        String content = buildEmailContent(unsentJobPostings);

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

            log.error("이메일 발송 실패 recipientEmail {}, 이유: {}", user.getEmail(), exception.getMessage(), exception);
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
