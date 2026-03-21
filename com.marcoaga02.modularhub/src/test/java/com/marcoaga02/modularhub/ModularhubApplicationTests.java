package com.marcoaga02.modularhub;

import com.marcoaga02.modularhub.config.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfig.class)
class ModularhubApplicationTests {

	@Test
	void contextLoads() {
	}

}
