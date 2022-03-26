package xwsbsep.bezbednostbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xwsbsep.bezbednostbackend.model.Certificate;
import xwsbsep.bezbednostbackend.model.User;
import xwsbsep.bezbednostbackend.repository.CertificateRepository;
import xwsbsep.bezbednostbackend.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class CertificateService {
    private CertificateRepository certificateRepository;
    private UserRepository userRepository;

    @Autowired
    public CertificateService(CertificateRepository certificateRepository, UserRepository userRepository){
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
    }

    public Collection<Certificate> getUsersCertificates(UUID id){
        User user = userRepository.getUserById(id);
        if(user.getId() == null)
            return null;
        else if (user.getUserRole().toString().equals("USER"))
            return certificateRepository.findAllBySubjectId(id);
        else if (user.getUserRole().toString().equals("ADMIN"))
            return certificateRepository.findAll();

        List<Certificate> retVal = new ArrayList<Certificate>();
        for (Certificate certificate: certificateRepository.findAll()){
            if (certificate.getSubject().getId().equals(id)) {
                retVal.add(certificate);
                continue;
            }

            if (certificate.getParentCertificate() != null){
                Certificate temp = certificate;
                do{
                    if (temp.getParentCertificate() == null) { //Zavisi kako cemo da oznacimo poslednji
                        break;
                    }
                    if (temp.getSubject().getId().equals(id)){
                        retVal.add(certificate);
                        break;
                    }
                    temp = temp.getParentCertificate();
                }while(true);
            }
        }
        return retVal;
    }
}
