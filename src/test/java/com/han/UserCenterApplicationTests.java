package com.han;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;




@SpringBootTest
class UserCenterApplicationTests {

	@Test
	void contextLoads() {
		final String SALT = "han";
		String password = "123";
		String s = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
		System.out.println(s);

	}

}
