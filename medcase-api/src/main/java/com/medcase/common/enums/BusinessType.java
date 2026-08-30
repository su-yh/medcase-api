package com.medcase.common.enums;

/**
 * 业务操作类型
 * 
 */
public enum BusinessType {

    /**
     * 其它
     */
    OTHER(0),

    /**
     * 新增
     */
    INSERT(1),

    /**
     * 修改
     */
    UPDATE(2),

    /**
     * 删除
     */
    DELETE(3),

    /**
     * 授权
     */
    GRANT(4),

    /**
     * 强退
     */
    FORCE(7),

    /**
     * 生成代码
     */
    GENCODE(8),
    
    /**
     * 清空数据
     */
    CLEAN(9);

    private final int code;

    BusinessType(int code) {

        this.code = code;
    }

    public int getCode() {

        return code;
    }
}
