package xwsbsep.bezbednostbackend;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xwsbsep.bezbednostbackend.model.Certificate;
import xwsbsep.bezbednostbackend.model.User;
import xwsbsep.bezbednostbackend.model.UserRole;
import xwsbsep.bezbednostbackend.repository.CertificateRepository;
import xwsbsep.bezbednostbackend.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootApplication
public class BezbednostBackendApplication implements CommandLineRunner {

	private UserRepository userRepository;
	private CertificateRepository certificateRepository;

	@Autowired
	public BezbednostBackendApplication(UserRepository userRepository,
										CertificateRepository certificateRepository) {
		this.userRepository = userRepository;
		this.certificateRepository = certificateRepository;
		Security.addProvider(new BouncyCastleProvider());
	}

	public static void main(String[] args) {
		SpringApplication.run(BezbednostBackendApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		User admin = new User("admin", "admin", hash("admin"), UserRole.ADMIN, "Srbija", "UNS");
		userRepository.save(admin);
		User obican = new User("obican", "obican", hash("obican"), UserRole.USER, "Srbija","FTN" );
		userRepository.save(obican);
		User inter = new User("inter", "inter", hash("inter"), UserRole.INTER, "Srbija", "FTN");
		userRepository.save(inter);

		Certificate root = new Certificate(
				"9999",
				LocalDateTime.of(2021, 1, 13, 0, 0),
				LocalDateTime.of(2033, 1, 13, 0, 0),
				null,
				true,
				false,
				admin,
				""
		);
		certificateRepository.save(root);

		Certificate certificate = new Certificate(
				"1234",
				LocalDateTime.of(2021, 1, 13, 0, 0),
				LocalDateTime.of(2023, 1, 13, 0, 0),
				root,
				true,
				false,
				inter,
				""
		);
		certificateRepository.save(certificate);

		Certificate certificate2 = new Certificate(
				"4567",
				LocalDateTime.of(2021, 1, 13, 0, 0),
				LocalDateTime.of(2023, 1, 13, 0, 0),
				certificate,
				false,
				false,
				obican,
				""
		);
		certificateRepository.save(certificate2);
	}

	private String hash(String data){
		try {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			byte[] dataHash = sha256.digest(data.getBytes());
			return new String(dataHash, StandardCharsets.UTF_8);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
