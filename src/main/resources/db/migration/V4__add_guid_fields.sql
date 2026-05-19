-- Add GUID fields to all entities for cross-service identification
-- PostgreSQL 18 supports UUID v7 natively

-- Add GUID to orders table
ALTER TABLE orders ADD COLUMN guid UUID NOT NULL DEFAULT gen_random_uuid();
CREATE UNIQUE INDEX idx_orders_guid ON orders(guid);

-- Add GUID to line_items table
ALTER TABLE line_items ADD COLUMN guid UUID NOT NULL DEFAULT gen_random_uuid();
CREATE UNIQUE INDEX idx_line_items_guid ON line_items(guid);

-- Add GUID to vehicles table
ALTER TABLE vehicles ADD COLUMN guid UUID NOT NULL DEFAULT gen_random_uuid();
CREATE UNIQUE INDEX idx_vehicles_guid ON vehicles(guid);

-- Add GUID to vrp_jobs table
ALTER TABLE vrp_jobs ADD COLUMN guid UUID NOT NULL DEFAULT gen_random_uuid();
CREATE UNIQUE INDEX idx_vrp_jobs_guid ON vrp_jobs(guid);

-- Add GUID to trips table
ALTER TABLE trips ADD COLUMN guid UUID NOT NULL DEFAULT gen_random_uuid();
CREATE UNIQUE INDEX idx_trips_guid ON trips(guid);

-- Add GUID to visits table
ALTER TABLE visits ADD COLUMN guid UUID NOT NULL DEFAULT gen_random_uuid();
CREATE UNIQUE INDEX idx_visits_guid ON visits(guid);

-- Add GUID to audit_log table
ALTER TABLE audit_log ADD COLUMN guid UUID NOT NULL DEFAULT gen_random_uuid();
CREATE UNIQUE INDEX idx_audit_log_guid ON audit_log(guid);
