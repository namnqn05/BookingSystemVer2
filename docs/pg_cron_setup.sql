-- =============================================================================
-- pg_cron Scheduler Setup Script for Booking System
-- Purpose: Automatically update PENDING bookings to EXPIRED when start_time <= NOW()
-- =============================================================================

-- Prerequisites:
-- 1. Add pg_cron to shared_preload_libraries in postgresql.conf:
--    shared_preload_libraries = 'pg_cron'
--    cron.database_name = 'booking_system'
-- 2. Restart PostgreSQL server.
-- 3. Run this script as PostgreSQL superuser (or database owner).

-- -----------------------------------------------------------------------------
-- 1. Enable pg_cron extension
-- -----------------------------------------------------------------------------
CREATE EXTENSION IF NOT EXISTS pg_cron;

-- -----------------------------------------------------------------------------
-- 2. Create PL/pgSQL function to expire overdue PENDING bookings
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION expire_overdue_bookings()
RETURNS void AS $$
DECLARE
    rows_updated integer;
BEGIN
    UPDATE bookings
    SET status = 'EXPIRED'
    WHERE status = 'PENDING'
      AND start_time <= NOW();

    GET DIAGNOSTICS rows_updated = ROW_COUNT;
    IF rows_updated > 0 THEN
        RAISE NOTICE 'Expired % pending booking(s) at %', rows_updated, NOW();
    END IF;
END;
$$ LANGUAGE plpgsql;

-- -----------------------------------------------------------------------------
-- 3. Schedule the job to run every minute
-- -----------------------------------------------------------------------------
-- Unschedule first if job already exists to avoid duplicate registrations
SELECT cron.unschedule('expire_pending_bookings_job')
WHERE EXISTS (
    SELECT 1 FROM cron.job WHERE jobname = 'expire_pending_bookings_job'
);

-- Register cron schedule (runs every minute: '* * * * *')
SELECT cron.schedule(
    'expire_pending_bookings_job',
    '* * * * *',
    'SELECT expire_overdue_bookings()'
);

-- -----------------------------------------------------------------------------
-- Useful Management & Diagnostic Queries
-- -----------------------------------------------------------------------------
-- View all scheduled cron jobs:
-- SELECT jobid, schedule, command, nodename, nodeport, database, username, active, jobname FROM cron.job;

-- View execution log history:
-- SELECT jobid, runid, job_pid, status, return_message, start_time, end_time FROM cron.job_run_details ORDER BY start_time DESC LIMIT 20;

-- Remove the scheduled job:
-- SELECT cron.unschedule('expire_pending_bookings_job');
