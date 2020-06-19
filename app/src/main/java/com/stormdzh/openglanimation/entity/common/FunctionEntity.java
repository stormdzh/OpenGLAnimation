package com.stormdzh.openglanimation.entity.common;

/**
 * @Description: 描述
 * @Author: dzh
 * @CreateDate: 2020-06-16 16:58
 */
public class FunctionEntity {
    public String name;
    public Class target;

    public FunctionEntity(String name, Class target) {
        this.name = name;
        this.target = target;
    }
}
