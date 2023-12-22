package io.trans;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest("-d D:\\\\A\\\\good\\\\女仆")
class TransApplicationTests {

	@Value("${d}")
	private String dir;

	@Test
	void contextLoads() {
		log.info(dir);
	}

}
