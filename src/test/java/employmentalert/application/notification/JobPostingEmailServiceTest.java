package employmentalert.application.notification;

import employmentalert.api.user.service.UserService;
import employmentalert.application.notification.email.JobPostingEmailService;
import employmentalert.domain.jobPosting.JobPosting;
import employmentalert.domain.jobPosting.repository.JobPostingQueryRepository;
import employmentalert.domain.notification.NotificationChannel;
import employmentalert.domain.notification.NotificationHistory;
import employmentalert.domain.user.User;
import employmentalert.global.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class JobPostingEmailServiceTest {

    private final User testUser = new User("test@example.com", "career", "education", "employmentType", "region");
    private final List<JobPosting> postings = List.of(
            new JobPosting("company", "title", "test.com", "career", "education", "employmentType", "region", "deadline")
    );

    @InjectMocks
    private JobPostingEmailService jobPostingEmailService;

    @Mock
    private JobPostingQueryRepository jobPostingQueryRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private UserService userService;
    @Mock
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        // 모든 테스트에서 공통으로 사용하는 유저 mock 설정
        when(userService.getUserById(anyLong())).thenReturn(testUser);
    }

    @Test
    @DisplayName("유저 한명에게 정상적으로 이메일이 발송된다.")
    void sendJobPostingEmailTest() {
        // given
        when(jobPostingQueryRepository.findUnsentJobPostings(anyString())).thenReturn(postings);
        doNothing().when(emailService).sendEmail(any());

        // when
        jobPostingEmailService.sendJobPostingEmails(List.of(1L));

        // then
        verify(emailService, times(1)).sendEmail(any());
    }

    @Test
    @DisplayName("다수의 유저에게 정상적으로 이메일이 발송된다.")
    void sendJobPostingEmailsTest() {
        // given
        when(jobPostingQueryRepository.findUnsentJobPostings(anyString())).thenReturn(postings);
        doNothing().when(emailService).sendEmail(any());

        // when
        jobPostingEmailService.sendJobPostingEmails(List.of(1L, 2L, 3L));

        // then
        verify(emailService, times(3)).sendEmail(any());
    }

    @Test
    @DisplayName("이메일 발송 성공 시 subject, content, channel이 올바르게 전달되고 성공 이력이 저장된다.")
    void sendEmailAndRecordSuccess() {
        // given
        when(jobPostingQueryRepository.findUnsentJobPostings(anyString())).thenReturn(postings);

        NotificationHistory successHistory = mock(NotificationHistory.class);
        when(notificationService.success(
                eq("test@example.com"),
                anyString(),
                anyString(),
                eq(NotificationChannel.EMAIL))
        ).thenReturn(successHistory);

        doNothing().when(emailService).sendEmail(any());

        // when
        jobPostingEmailService.sendJobPostingEmails(List.of(1L));

        // then
        ArgumentCaptor<String> subject = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> content = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<NotificationChannel> notificationChannel = ArgumentCaptor.forClass(NotificationChannel.class);

        verify(notificationService).success(
                eq("test@example.com"),
                subject.capture(),
                content.capture(),
                notificationChannel.capture()
        );

        assertThat(subject.getValue()).contains("채용공고");
        assertThat(content.getValue()).contains("회사명");
        assertThat(notificationChannel.getValue()).isEqualTo(NotificationChannel.EMAIL);

        verify(notificationService, times(postings.size()))
                .createRecord(eq(testUser), any(JobPosting.class), eq(successHistory));
    }

    @Test
    @DisplayName("이메일 발송에 실패 시 에러 로그와 함께 알림이력에 저장된다.")
    void sendEmailAndRecordFail() {
        // given
        when(jobPostingQueryRepository.findUnsentJobPostings(anyString())).thenReturn(postings);
        doThrow(new RuntimeException("Exception")).when(emailService).sendEmail(any());

        // when
        jobPostingEmailService.sendJobPostingEmails(List.of(1L));

        // then
        verify(notificationService, times(1)).fail(
                eq("test@example.com"),
                anyString(),
                anyString(),
                eq(NotificationChannel.EMAIL),
                eq("Exception")
        );
    }

}
