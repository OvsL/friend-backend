package org.example.friend.util;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Session工具类
 */
@Component
@RequiredArgsConstructor
public class SessionUtil {

    /**
     * 获取当前Session
     */
    public HttpSession getCurrentSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest().getSession(true);
        }
        return null;
    }

    /**
     * 设置Session属性
     */
    public void setAttribute(String key, Object value) {
        HttpSession session = getCurrentSession();
        if (session != null) {
            session.setAttribute(key, value);
        }
    }

    /**
     * 获取Session属性
     */
    public Object getAttribute(String key) {
        HttpSession session = getCurrentSession();
        if (session != null) {
            return session.getAttribute(key);
        }
        return null;
    }

    /**
     * 移除Session属性
     */
    public void removeAttribute(String key) {
        HttpSession session = getCurrentSession();
        if (session != null) {
            session.removeAttribute(key);
        }
    }

    /**
     * 使Session失效
     */
    public void invalidate() {
        HttpSession session = getCurrentSession();
        if (session != null) {
            session.invalidate();
        }
    }
}