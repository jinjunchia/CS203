package com.cs203.cs203system.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public class Notification {
        private NotificationStatus status;
        private String message;
        private String Winner;
        private String tournament;
    }
