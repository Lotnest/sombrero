package dev.lotnest.sombrero.bugreport;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "bug_report")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
public class BugReport {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bug_report_seq")
    @SequenceGenerator(name = "bug_report_seq", sequenceName = "bug_report_seq", allocationSize = 1)
    private Long id;

    @Column(name = "title")
    @NotBlank
    private String title;

    @Column(name = "description")
    @NotBlank
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "is_fixed")
    private boolean isFixed;

    @Column(name = "changes")
    private String changes;
}
