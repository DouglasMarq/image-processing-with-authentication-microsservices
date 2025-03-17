package com.douglasmarq.auth.infraestructure.logs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AnonymizeLogger {

    private static final ObjectMapper MAPPER =
            new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    private final Logger logger;
    private final boolean anonymizationOn = true;

    public AnonymizeLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    private Object anonymize(Object target) {
        try {
            if (needAnonymize(target)) {
                if (target instanceof List<?>) {
                    return anonymizeArrayParams((List<?>) target);
                }
                if (target instanceof String) {
                    return anonymize((String) target);
                }
                Map<String, Object> map = convertObjectToMap(target);
                return Anonymizer.desensitizeData(map, true);
            }
        } catch (Exception e) {
            logger.debug("error on anonymize {}", target);
        }
        return target;
    }

    private String anonymize(String target) {
        if (target.contains("@")) {
            String[] parts = target.split("@", 2);
            if (parts.length == 2) {
                String local = parts[0];
                String domain = parts[1];

                if (!local.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(local.charAt(0));
                    for (int i = 1; i < local.length(); i++) {
                        sb.append("*");
                    }
                    local = sb.toString();
                }
                return local + "@" + domain;
            }
        }

        return target;
    }

    private Map<String, Object> convertObjectToMap(Object target) throws Exception {
        return MAPPER.convertValue(target, new TypeReference<Map<String, Object>>() {});
    }

    private Object[] anonymizeArrayParams(Object... args) {
        Object[] anonymized = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            anonymized[i] = anonymize(args[i]);
        }
        return anonymized;
    }

    private Object[] anonymizeArrayParams(List<?> args) {
        Object[] anonymized = new Object[args.size()];
        for (int i = 0; i < args.size(); i++) {
            anonymized[i] = anonymize(args.get(i));
        }
        return anonymized;
    }

    private boolean needAnonymize(Object object) {
        return anonymizationOn
                && (object == null
                        || object instanceof String
                        || object instanceof UUID
                        || object instanceof Exception
                        || object instanceof Integer
                        || object instanceof Long
                        || object.getClass().isEnum());
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void info(String format, Object arg) {
        logger.info(format, anonymize(arg));
    }

    public void info(String format, Object arg, Object arg1) {
        logger.info(format, anonymize(arg), anonymize(arg1));
    }

    public void info(String format, Object... args) {
        logger.info(format, anonymizeArrayParams(args));
    }

    public void debug(String msg) {
        logger.debug(msg);
    }

    public void debug(String format, Object arg) {
        logger.debug(format, anonymize(arg));
    }

    public void debug(String format, Object arg, Object arg1) {
        logger.debug(format, anonymize(arg), anonymize(arg1));
    }

    public void warn(String msg) {
        logger.warn(msg);
    }

    public void warn(String format, Object... args) {
        logger.warn(format, anonymizeArrayParams(args));
    }

    public void error(String msg) {
        logger.error(msg);
    }

    public void error(String format, Object... args) {
        logger.error(format, anonymizeArrayParams(args));
    }

    public void error(String msg, Exception e) {
        logger.error(msg, e);
    }
}
