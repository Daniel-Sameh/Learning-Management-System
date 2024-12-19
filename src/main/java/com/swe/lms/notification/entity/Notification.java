package com.swe.lms.notification.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swe.lms.userManagement.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean read = false;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "notifications", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();
    public void addUser(User user) {
        users.add(user);
        user.getNotifications().add(this);
    }

}
