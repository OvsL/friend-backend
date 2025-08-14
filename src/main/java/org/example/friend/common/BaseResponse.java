package org.example.friend.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
/**
 * 通用返回类
 * @param <T>
 */
@Data
@Schema(description = "通用响应结果")
public class BaseResponse <T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -4960386077641441273L;

    @Schema(description = "状态码", example = "0")
    private int code;

    @Schema(description = "响应信息", example = "ok")
    private String msg;

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "错误描述")
    private String description;

    public BaseResponse(int code, T data, String msg, String description) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.description = description;
    }

    public BaseResponse(int code, T data, String msg) {
        this(code, data, msg, "");
    }

    public BaseResponse(int code, T data) {
        this(code, data, "", "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }


}
