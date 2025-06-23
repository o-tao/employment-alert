package employmentalert.global;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class ExecutionTimeLogger {

    @Around("execution(* employmentalert.api..service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        String methodName = joinPoint.getSignature().toShortString();

        stopWatch.start(methodName);

        try {
            return joinPoint.proceed();
        } finally {
            stopWatch.stop();
            log.info("⏱️ [{}] 실행 시간: {}ms", methodName, stopWatch.lastTaskInfo().getTimeMillis());
        }
    }
}
