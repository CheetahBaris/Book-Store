package com.example.MyBookShopApp.annotations;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.logging.Logger;

@Aspect
@Component
public class exLogAspect {
    Logger logger = Logger.getLogger(getClass().getName());
    @Pointcut(value = "within(com.example.MyBookShopApp.controllers.*)")
    public void allBeansFromControllersPackagePointcut(){}

    @AfterThrowing(pointcut = "allBeansFromControllersPackagePointcut()", throwing = "ex")
    public void logExFromAllControllersAdvice(JoinPoint joinPoint, Exception ex){

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        logger.warning("Something went wrong in \""+method.getName()+"\" ; Ex is - " + ex.getMessage());
    }
}
