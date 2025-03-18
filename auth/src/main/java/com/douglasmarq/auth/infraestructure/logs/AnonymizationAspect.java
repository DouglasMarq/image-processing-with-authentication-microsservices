package com.douglasmarq.auth.infraestructure.logs;

import lombok.NoArgsConstructor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
@NoArgsConstructor
public class AnonymizationAspect {

    @Around(
            "@within(com.douglasmarq.auth.infraestructure.logs.Anonymize) || @annotation(com.douglasmarq.auth.infraestructure.logs.Anonymize)")
    public Object anonymize(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed(joinPoint.getArgs());
        if (result instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> castedMap = (Map<String, Object>) result;
            return Anonymizer.desensitizeData(castedMap);
        }
        return result;
    }
}
