package org.aryak.batch.replay;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "failed_lines")
public class FailedLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_execution_id")
    private Long jobExecutionId;

    @Column(name = "line_number")
    private Integer lineNumber;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "processed")
    private Boolean processed;

}