package com.medcase.mvc.user;

import lombok.Getter;

/**
 * @author suyh
 * @since 2025-05-20
 */
@Getter
public abstract class AbstractLoginUser {
    public abstract Long getId();

    public abstract String getNickname();

    public abstract String getUsername();
}
