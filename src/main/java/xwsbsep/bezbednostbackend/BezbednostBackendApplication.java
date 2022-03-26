package xwsbsep.bezbednostbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xwsbsep.bezbednostbackend.model.User;
import xwsbsep.bezbednostbackend.model.UserRole;
import xwsbsep.bezbednostbackend.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class BezbednostBackendApplication implements CommandLineRunner {

	private UserRepository userRepository;

	@Autowired
	public BezbednostBackendApplication(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(BezbednostBackendApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		User admin = new User("admin", "admin", hash("admin"), UserRole.ADMIN);
		userRepository.save(admin);
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
