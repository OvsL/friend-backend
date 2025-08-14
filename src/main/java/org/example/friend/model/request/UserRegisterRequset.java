package org.example.friend.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求体
 */
@Data
@Schema(description = "用户注册请求")
public class UserRegisterRequset implements Serializable {

    @Serial
    private static final long serialVersionUID = 6180395477493225658L;

    @Schema(description = "用户账号", example = "testuser")
    private String userAccount;
    
    @Schema(description = "用户密码", example = "12345678")
    private String userPassword;
    
    @Schema(description = "确认密码", example = "12345678")
    private String checkPassword;
}
