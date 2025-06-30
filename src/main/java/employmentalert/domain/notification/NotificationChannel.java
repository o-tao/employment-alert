package employmentalert.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationChannel {

    EMAIL("이메일");

    private final String notificationChannel;
}
