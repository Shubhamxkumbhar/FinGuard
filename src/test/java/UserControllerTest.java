import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = com.finguard.userservice.UserServiceApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest extends com.finguard.userservice.controller.UserControllerTest {
}
