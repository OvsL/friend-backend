package org.example.friend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

@SpringBootTest
class FriendApplicationTests {


    @Test
    void testEncrypt() {
        String originPassword = "12345678";
        String salt = "qi";
        String result = DigestUtils.md5DigestAsHex((salt + originPassword).getBytes());
        System.out.println(result);
    }

    @Test
    void contextLoads() {
    }

}
