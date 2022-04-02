package xwsbsep.bezbednostbackend.service;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import xwsbsep.bezbednostbackend.dto.NewCertificateDto;
import xwsbsep.bezbednostbackend.model.Certificate;
import xwsbsep.bezbednostbackend.model.User;
import xwsbsep.bezbednostbackend.model.data.IssuerData;
import xwsbsep.bezbednostbackend.model.data.SubjectData;
import xwsbsep.bezbednostbackend.repository.CertificateRepository;
import xwsbsep.bezbednostbackend.repository.UserRepository;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class CertificateService {
    private CertificateRepository certificateRepository;
    private UserRepository userRepository;
    private KeyStoreWriter keyStoreWriter;
    private KeyStoreReader keyStoreReader;

    //smisliti gde da se izmesti
    private final String passRoot;
    private final String passInter;
    private final String passEnd;

    @Autowired
    public CertificateService(CertificateRepository certificateRepository,
                              UserRepository userRepository,
                              Environment environment) {
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
        this.keyStoreReader = new KeyStoreReader();
        this.keyStoreWriter = new KeyStoreWriter();
        this.passRoot = environment.getProperty("KeyStoreAdmin");
        this.passInter = environment.getProperty("KeyStoreInter");
        this.passEnd = environment.getProperty("KeyStoreUser");
    }

    public Certificate createNewCertificate(NewCertificateDto newCertificate) {
        Certificate certificate;
        IssuerData issuerData;
        KeyPair keyPairSubject = generateKeyPair();
        if (newCertificate.isSelfSigned) {
            certificate = CreateSelfSigned(newCertificate);
            issuerData = generateIssuerData(keyPairSubject.getPrivate(), certificate.getSubject());
        } else {
            certificate = CreateCertificate(newCertificate);
            String parentPass = getPassword(certificate.getParentCertificate().getKeystorePath());
            PrivateKey privateKey = keyStoreReader.readPrivateKey(certificate.getParentCertificate().getKeystorePath(), parentPass, certificate.getParentCertificate().getSerialNumber(), parentPass);
            issuerData = generateIssuerData(privateKey, certificate.getParentCertificate().getSubject());
            if (!ValidateCertificateOnCreation(certificate.getParentCertificate().getId(), null))
                return null;
        }

        certificateRepository.save(certificate);

        SubjectData subjectData = generateSubjectData(keyPairSubject.getPublic(), certificate);
        X509Certificate cert = generateCertificate(subjectData, issuerData);

        String pass = getPassword(certificate.getKeystorePath());
        keyStoreWriter.loadKeyStore(certificate.getKeystorePath(), pass.toCharArray());
        keyStoreWriter.write(certificate.getSerialNumber(), keyPairSubject.getPrivate(), pass.toCharArray(), cert);
        keyStoreWriter.saveKeyStore(certificate.getKeystorePath(), pass.toCharArray());

        return certificate;
    }

    private Certificate CreateSelfSigned(NewCertificateDto newCertificate) {
        Random rand = new Random();
        Certificate certificate = new Certificate();
        certificate.setCA(true);
        certificate.setStartDate(newCertificate.startDate);
        certificate.setEndDate(newCertificate.endDate);
        certificate.setRevoked(false);
        if (newCertificate.isSelfSigned) {
            certificate.setKeystorePath("root.jks");
        }
        certificate.setSerialNumber(rand.nextInt(50000) + "");
        certificate.setSubject(userRepository.getUserById(UUID.fromString(newCertificate.userId)));
        return certificate;
    }

    private Certificate CreateCertificate(NewCertificateDto newCertificate) {
        Random rand = new Random();
        Certificate certificate = new Certificate();
        Certificate parent = certificateRepository.getById(UUID.fromString(newCertificate.parentId));
        certificate.setParentCertificate(parent);
        certificate.setCA(newCertificate.isCA);
        certificate.setStartDate(newCertificate.startDate);
        certificate.setEndDate(newCertificate.endDate);
        certificate.setRevoked(false);
        if (newCertificate.isCA) {
            certificate.setKeystorePath("intermediate.jks");
        } else {
            certificate.setKeystorePath("end.jks");
        }
        certificate.setSerialNumber(rand.nextInt(50000) + "");
        certificate.setSubject(userRepository.getUserById(UUID.fromString(newCertificate.userId)));

        return certificate;
    }


    public Collection<Certificate> getUsersCertificates(UUID id) {
        User user = userRepository.getUserById(id);
        if (user.getId() == null)
            return null;
        else if (user.getUserRole().toString().equals("USER"))
            return certificateRepository.findAllBySubjectId(id);
        else if (user.getUserRole().toString().equals("ADMIN"))
            return certificateRepository.findAll();

        List<Certificate> retVal = new ArrayList<Certificate>();
        for (Certificate certificate : certificateRepository.findAll()) {
            if (certificate.getSubject().getId().equals(id)) {
                retVal.add(certificate);
                continue;
            }

            if (certificate.getParentCertificate() != null) {
                Certificate temp = certificate;
                do {
                    if (temp.getParentCertificate() == null) { //Zavisi kako cemo da oznacimo poslednji
                        break;
                    }
                    if (temp.getSubject().getId().equals(id)) {
                        retVal.add(certificate);
                        break;
                    }
                    temp = temp.getParentCertificate();
                } while (true);
            }
        }
        return retVal;
    }

    public boolean ValidateCertificateOnCreation(UUID id, String serialNumber) {
        Certificate certificate;

        if (serialNumber == null) {
            certificate = certificateRepository.getById(id);
        }
        else{
            certificate = certificateRepository.findBySerialNumber(serialNumber);
        }

        if (certificate == null) {
            return false;
        }
        while (certificate.getParentCertificate() != null) {
            if (certificate.isRevoked())
                return false;
            //posle za proveru radnog sertifikata se proveri i da li je poceo da vazi
            if (certificate.getEndDate().isBefore(LocalDateTime.now())) {
                return false;
            }
            java.security.cert.Certificate certificateKS =
                    keyStoreReader.readCertificate(
                            certificate.getKeystorePath(),
                            getPassword(certificate.getKeystorePath()),
                            certificate.getSerialNumber());

            java.security.cert.Certificate parentCertificateKS = keyStoreReader.readCertificate(
                    certificate.getParentCertificate().getKeystorePath(),
                    getPassword(certificate.getParentCertificate().getKeystorePath()),
                    certificate.getParentCertificate().getSerialNumber());
            try {
                certificateKS.verify(parentCertificateKS.getPublicKey());
            } catch (SignatureException | CertificateException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException e) {
                return false;
            }
            certificate = certificate.getParentCertificate();
        }
        if (certificate.isRevoked())
            return false;
        //posle za proveru radnog sertifikata se proveri i da li je poceo da vazi
        if (certificate.getEndDate().

                isBefore(LocalDateTime.now())) {
            return false;
        }
        return true;
    }

    public Certificate revokeCertificate(UUID id) {
        Certificate certificate = certificateRepository.getById(id);
        certificate.setRevoked(true);
        return certificateRepository.save(certificate);
    }

    private X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData) {
        try {

            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            builder = builder.setProvider("BC");
            ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
                    new BigInteger(subjectData.getSerialNumber()),
                    subjectData.getStartDate(),
                    subjectData.getEndDate(),
                    subjectData.getX500name(),
                    subjectData.getPublicKey());
            X509CertificateHolder certHolder = certGen.build(contentSigner);

            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");
            return certConverter.getCertificate(certHolder);
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getPassword(String path) {
        switch (path) {
            case "root.jks":
                return passRoot;
            case "intermediate.jks":
                return passInter;
            default:
                return passEnd;
        }
    }

    private IssuerData generateIssuerData(PrivateKey issuerKey, User parent) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, parent.getName());
        builder.addRDN(BCStyle.C, parent.getCountry());
        builder.addRDN(BCStyle.O, parent.getOrganization());
        builder.addRDN(BCStyle.UID, parent.getId().toString());

        return new IssuerData(builder.build(), issuerKey);
    }

    private SubjectData generateSubjectData(PublicKey publicKey, Certificate certificate) {
        Date startDate = Date.from(certificate.getStartDate().atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(certificate.getEndDate().atZone(ZoneId.systemDefault()).toInstant());

        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, certificate.getSubject().getName());
        builder.addRDN(BCStyle.O, certificate.getSubject().getOrganization());
        builder.addRDN(BCStyle.C, certificate.getSubject().getCountry());
        builder.addRDN(BCStyle.UID, certificate.getSubject().getId().toString());
        return new SubjectData(publicKey, builder.build(), certificate.getSerialNumber(), startDate, endDate);
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }
}
