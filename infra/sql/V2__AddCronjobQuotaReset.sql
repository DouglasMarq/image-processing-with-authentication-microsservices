CREATE EXTENSION IF NOT EXISTS pg_cron;

SELECT cron.schedule(
    'reset_quotas_daily',
    '0 0 * * *',
    $$
      UPDATE users
      SET quotas = 10
      WHERE plan = 'BASIC';
    $$
);
