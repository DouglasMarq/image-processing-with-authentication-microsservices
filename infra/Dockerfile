FROM postgres:17.4-alpine3.21

ENV POSTGRES_DB="database"
ENV POSTGRES_USER="username"
ENV POSTGRES_PASSWORD="password"

RUN apk add --no-cache postgresql-pg_cron && \
    cp /usr/lib/postgresql17/pg_cron.so /usr/local/lib/postgresql/ && \
    cp /usr/share/postgresql17/extension/* /usr/local/share/postgresql/extension

COPY setup-pg-cron.sh /docker-entrypoint-initdb.d/
