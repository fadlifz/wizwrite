package com.wizwrite.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "content_history")
@Data
public class ContentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String prompt;
    @Column(columnDefinition = "TEXT")
    private String responseText;
    private LocalDateTime createdAt;
}
