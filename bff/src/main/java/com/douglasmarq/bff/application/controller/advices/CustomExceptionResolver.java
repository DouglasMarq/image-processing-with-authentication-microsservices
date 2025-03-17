package com.douglasmarq.bff.application.controller.advices;

import com.douglasmarq.bff.domain.exception.QuotaException;
import com.douglasmarq.bff.domain.exception.UserException;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class CustomExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof QuotaException quotaException) {
            return GraphqlErrorBuilder.newError(env)
                    .message(quotaException.getMessage())
                    .errorType(ErrorType.FORBIDDEN)
                    .build();
        }

        if (ex instanceof UserException userException) {
            return GraphqlErrorBuilder.newError(env)
                    .message(userException.getMessage())
                    .errorType(ErrorType.NOT_FOUND)
                    .build();
        }

        return super.resolveToSingleError(ex, env);
    }
}
