package ru.y_lab.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * UserAuditAspect class for logging and auditing user actions.
 */
@Aspect
@Component
public class UserAuditAspect {

    /**
     * Pointcut that matches all methods annotated with @Loggable.
     */
    @Pointcut("within(@ru.y_lab.annotation.Loggable *) && execution(* * (..))")
    public void annotatedByLoggable() {}

    /**
     * Advice that logs method calls and execution time.
     * @param proceedingJoinPoint the join point representing the method call
     * @return the result of the method execution
     * @throws Throwable if the method throws an exception
     */
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

    /**
     * Pointcut that matches all methods in the service package excluding lambda expressions.
     */
    @Pointcut("execution(* ru.y_lab.service..*(..)) && !execution(* *.lambda$*(..))")
    public void userActions() {}

    /**
     * Advice that audits user actions before the method execution.
     * @param joinPoint the join point representing the method call
     */
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
