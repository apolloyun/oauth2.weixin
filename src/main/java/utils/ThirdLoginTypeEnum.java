package utils;

public enum ThirdLoginTypeEnum {
    WEXIN("wx","微信"),QQ("qq","QQ"),WEIBO("wb","微博");
    private String code;
    private String description;

    ThirdLoginTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
