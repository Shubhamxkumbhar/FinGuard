import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/api/secure")
    public String secureEndpoint() {
        return "Access granted to secured endpoint!";
    }
}
