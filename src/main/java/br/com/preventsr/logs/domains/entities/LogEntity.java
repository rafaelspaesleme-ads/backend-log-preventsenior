package br.com.preventsr.logs.domains.entities;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "tab_logs")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(setterPrefix = "with", toBuilder = true)
public class LogEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false, unique = true)
    private String id;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "date_time")
    private LocalDateTime dateTime;
    @Column(name = "ip")
    private String ip;
    @Column(name = "request")
    private String request;
    @Column(name = "status_http")
    private String statusHttp;
    @Column(name = "user_agent")
    private String userAgent;
    @Column(name = "active")
    private Boolean active;
}
