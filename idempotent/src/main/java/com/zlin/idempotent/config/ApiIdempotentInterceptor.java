package com.zlin.idempotent.config;

import com.zlin.idempotent.controller.TokenController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author zlin
 * @date 20220521
 */
public class ApiIdempotentInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenController tokenController;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod= (HandlerMethod) handler;
        Method method=handlerMethod.getMethod();

        ApiIdempotent methodAnnotation=method.getAnnotation(ApiIdempotent.class);
        if (methodAnnotation != null) {
            // 校验通过放行，校验不通过全局异常捕获后输出返回结果
            tokenController.checkIdempotentToken(request);
        }
        return true;
    }


    /**
     * 请求controller之后，渲染视图之前
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    /**
     * 请求controller之后，渲染视图之后
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }


}
