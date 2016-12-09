package com.example.todo.api;

import javax.ws.rs.core.SecurityContext;

/**
 * Created by Jeff on 11/21/16.
 */
public class OperationContext {
    private SecurityContext m_secCtx = null;

    public OperationContext(SecurityContext ctx) {
        m_secCtx = ctx;
    }

    public SecurityContext getSecurityContext() {
        return this.m_secCtx;
    }
}
