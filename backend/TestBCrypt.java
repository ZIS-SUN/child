import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Generate hash for admin123
        String password = "admin123";
        String hash = encoder.encode(password);
        System.out.println("Generated hash: " + hash);

        // Test the hash from SQL file
        String sqlHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lk9o8.5cw7FqK7rGW";
        boolean matches = encoder.matches(password, sqlHash);
        System.out.println("Does 'admin123' match SQL hash? " + matches);
    }
}
