package org.example.friend.constant;


import lombok.Getter;


@Getter
public enum TeamStatusEnum {
    /**
     * 公开
     */
    PUBLIC(0, "公开"),
    /**
     * 私有
     */
    PRIVATE(1, "私有"),
    /**
     * 加密
     */
    SECRET(2, "加密");

    private final int value;

    private final String text;

    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据value获取枚举
     */
    public static TeamStatusEnum getTeamStatusEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (TeamStatusEnum teamStatusEnum : TeamStatusEnum.values()) {
            if (teamStatusEnum.getValue() == value) {
                return teamStatusEnum;
            }
        }
        return null;
    }
}
