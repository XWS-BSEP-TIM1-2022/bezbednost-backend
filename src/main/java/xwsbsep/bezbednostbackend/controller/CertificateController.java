package xwsbsep.bezbednostbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xwsbsep.bezbednostbackend.dto.NewCertificateDto;
import xwsbsep.bezbednostbackend.model.Certificate;
import xwsbsep.bezbednostbackend.service.CertificateService;

import java.security.cert.CertificateEncodingException;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
public class CertificateController {
    private CertificateService certificateService;

    @Autowired
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Collection<Certificate>> getAllUsersCertificates(@PathVariable UUID id){
        Collection<Certificate> retVal = certificateService.getUsersCertificates(id);
        return new ResponseEntity<>(retVal, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Certificate> createNewCertificate(@RequestBody NewCertificateDto newCertificate){
        Certificate certificate = certificateService.createNewCertificate(newCertificate);
        if (certificate == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(certificate, HttpStatus.CREATED);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Certificate> revokeCertificate(@PathVariable UUID id, @RequestBody Certificate certificate) {
        Certificate revokedCertificate = certificateService.revokeCertificate(id, certificate.getRevokeReason());
        if (revokedCertificate == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(revokedCertificate, HttpStatus.OK);
    }

    @GetMapping("/verify-certificate/{serialNumber}")
    public ResponseEntity<Boolean> verifyCertificate(@PathVariable String serialNumber){
        return ResponseEntity.ok(certificateService.ValidateCertificateOnCreation(null, serialNumber));
    }

    @GetMapping(value = "/download/{serialNumber}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> download(@PathVariable String serialNumber){
        try {
            return ResponseEntity.ok(certificateService.download(serialNumber));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
