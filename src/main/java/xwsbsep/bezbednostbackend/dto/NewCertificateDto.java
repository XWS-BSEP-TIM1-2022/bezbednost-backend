package xwsbsep.bezbednostbackend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class NewCertificateDto {
    public String parentId;
    public String userId;
    public LocalDateTime startDate;
    public LocalDateTime endDate;
    public boolean isCA;
    public boolean isSelfSigned;
    public boolean clientAuth;
    public boolean serverAuth;
    public boolean emailProtection;
    public boolean codeSigning;
}
