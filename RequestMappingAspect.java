package com.myservice.service.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * <p>
 * This class enables the tracking of REST call entry / exit events for debugging and test purposes.
 * </p>
 * <p>
 * This class must NOT be enabled for production usage except in limited load scenarios due to the processing and I/O overhead incurred
 * </p>
 */
@Aspect
@Component
public class RequestMappingAspect {

    private final static Logger logger = LoggerFactory.getLogger(RequestMappingAspect.class);
    private final static int MAX_PARAM_LENGTH = 20;

    /**
     * Pointcut for public methods returning a ListenableFuture
     */
    @Pointcut("execution(public rx.Observable *(..))")
    private void anyPublicOperation() {
    }

    /**
     * Pointcut for V1_0 controllers
     */
    @Pointcut("within(com.myservice.service.controller.v1_0.*)")
    private void inV1() {
    }

    /**
     * Pointcut for V1_0 rules
     */
    @Pointcut("within(com.myservice.service.controller.v1_0.rule.*)")
    private void inRule() {
    }

    /**
     * Pointcut used to exclude the error controller
     */
    @Pointcut("within(com.myservice.service.controller.v1_0.AppErrorController)")
    private void notInErrorRule() {
    }

    /**
     * Pointcut to identify all controllers
     */
    @Pointcut("inV1() || inRule()")
    public void controller() {
    }

    /**
     * Pointcut for identify public methods in controllers, excluding the error controller
     */
    @Pointcut("controller() && anyPublicOperation() && !notInErrorRule()")
    public void restCall() {
    }

    /**
     * Flag method entry
     *
     * @param joinPoint Join point being processed
     */
    @Before("restCall()")
    public void before(JoinPoint joinPoint) {
        if (logger.isDebugEnabled()) {
            logger.debug("Call entry(" + Thread.currentThread() + ") @ " + getMessage(joinPoint));
        }
    }

    /**
     * Flag method exit
     *
     * @param joinPoint Join point being processed
     */
    @After("restCall()")
    public void after(JoinPoint joinPoint) {
        if (logger.isDebugEnabled()) {
            logger.debug("Call exit(" + Thread.currentThread() + ") @ " + getMessage(joinPoint));
        }
    }

    /**
     * Convert parameters into printable text so as to be identifiable (Long parameter contents will be curtialed)
     *
     * @param joinPoint Join point being processed
     * @return Diagnostic message
     */
    private String getMessage(JoinPoint joinPoint) {
        String params = "";
        Object[] args = joinPoint.getArgs();
        if ((null != args) && (args.length > 0)) {
            StringBuilder sb = new StringBuilder(100);
            for (Object arg : args) {
                // check if a null argument was passed over
                if (null == arg) {
                    sb.append("null,");
                } else {
                    String contents = arg.toString();
                    // If too long, shorten and notify by ellipsis
                    if (contents.length() > MAX_PARAM_LENGTH) {
                        contents = contents.substring(0, MAX_PARAM_LENGTH) + "...";
                    }
                    sb.append(contents).append(',');
                }
            }
            params = sb.substring(0, sb.length() - 1);
        }
        // Build message
        String target = joinPoint.getTarget().toString().split("@")[0];
        String method = joinPoint.getSignature().toShortString();
        return target + " (" + method.replace("..", params) + ")";
    }
}

