package com.aaseya.AIS;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Disabled because full application context requires external dependencies (Camunda/Zeebe/DB)")
class AisApplicationTests {

    void contextLoads() {
        // intentionally disabled
    }
}