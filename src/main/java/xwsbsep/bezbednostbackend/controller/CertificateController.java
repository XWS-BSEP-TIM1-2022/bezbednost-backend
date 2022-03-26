package xwsbsep.bezbednostbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xwsbsep.bezbednostbackend.model.Certificate;
import xwsbsep.bezbednostbackend.service.CertificateService;

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

}
