package xwsbsep.bezbednostbackend.model;

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

    private Date startDate;

    private Date endDate;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Certificate parentCertificate;

    private boolean isCA;

    private boolean isRevoked;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private User subject;

    private String keystorePath;

    public Certificate(){

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
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
