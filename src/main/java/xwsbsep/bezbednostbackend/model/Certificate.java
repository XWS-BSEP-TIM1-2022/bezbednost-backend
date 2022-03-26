package xwsbsep.bezbednostbackend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="CERTIFICATES")
public class Certificate {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String serialNumber;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime startDate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime endDate;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Certificate parentCertificate;

    private boolean isCA;

    private boolean isRevoked;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private User subject;

    private String keystorePath;

    public Certificate(){}

    public Certificate(String serialNumber, LocalDateTime startDate, LocalDateTime endDate, Certificate parentCertificate,
                       boolean isCA, boolean isRevoked, User subject, String keystorePath) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.parentCertificate = parentCertificate;
        this.isCA = isCA;
        this.isRevoked = isRevoked;
        this.subject = subject;
        this.keystorePath = keystorePath;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Certificate getParentCertificate() {
        return parentCertificate;
    }

    public void setParentCertificate(Certificate parentCertificate) {
        this.parentCertificate = parentCertificate;
    }

    public boolean isCA() {
        return isCA;
    }

    public void setCA(boolean CA) {
        isCA = CA;
    }

    public boolean isRevoked() {
        return isRevoked;
    }

    public void setRevoked(boolean revoked) {
        isRevoked = revoked;
    }

    public User getSubject() {
        return subject;
    }

    public void setSubject(User subject) {
        this.subject = subject;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
    }
}
