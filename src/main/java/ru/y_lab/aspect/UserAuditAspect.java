package ru.y_lab.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Arrays;

@Aspect
public class UserAuditAspect {

    @Pointcut("within(@ru.y_lab.annotation.Loggable *) && execution(* * (..))")
    public void annotatedByLoggable() {}

    @Around("annotatedByLoggable()")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String className = proceedingJoinPoint.getSignature().getDeclaringTypeName();
        String methodName = proceedingJoinPoint.getSignature().getName();
        Object[] methodArgs = proceedingJoinPoint.getArgs();

        System.out.printf("\u001B[34mCalling method: %s.%s(%s)\u001B[0m%n",
                className,
                methodName,
                Arrays.toString(methodArgs)
        );

        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis() - startTime;

        System.out.printf("\u001B[32mExecution finished: %s.%s(%s). Time: %dms.\u001B[0m%n",
                className,
                methodName,
                Arrays.toString(methodArgs),
                endTime
        );

        return result;
    }

    @Pointcut("execution(* ru.y_lab.service..*(..)) && !execution(* *.lambda$*(..))")
    public void userActions() {}

    @Before("userActions()")
    public void auditUserAction(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();

        System.out.printf("\u001B[33mUser action: %s.%s(%s)\u001B[0m%n",
                className,
                methodName,
                Arrays.toString(methodArgs)
        );
    }
}
