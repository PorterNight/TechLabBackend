package techlab.backend.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Techlab Backend Api",
                description = "Techlab.com", version = "1.0.1",
                contact = @Contact(
                        name = "Gainutdinov Renat",
                        email = "renatgh@gmail.com"
                )
        )
)
public class OpenApiConfig {
}
