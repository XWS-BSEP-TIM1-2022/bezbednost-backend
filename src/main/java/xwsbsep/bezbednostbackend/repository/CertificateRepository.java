package xwsbsep.bezbednostbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xwsbsep.bezbednostbackend.model.Certificate;
import xwsbsep.bezbednostbackend.model.User;

import java.util.List;
import java.util.UUID;

public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
    @Query("select c from Certificate c where c.subject.id = ?1")
    List<Certificate> findAllBySubjectId(UUID subjectId);

    @Query("select c from Certificate c where c.parentCertificate.id = ?1 and c.revoked = false")
    List<Certificate> findAllUnrevokedChildrenByParentId(UUID parentId);

    Certificate findBySerialNumber(String serialNumber);
}
