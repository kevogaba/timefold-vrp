-- Orders
CREATE TABLE orders (
    id                      VARCHAR(36) PRIMARY KEY,
    organization_id         VARCHAR(36) NOT NULL,
    customer_id             VARCHAR(36) NOT NULL,
    customer_name           VARCHAR(255) NOT NULL,
    customer_email          VARCHAR(255) NOT NULL,
    customer_address        TEXT NOT NULL,
    customer_latitude       DOUBLE PRECISION NOT NULL,
    customer_longitude      DOUBLE PRECISION NOT NULL,
    delivery_window_start   TIMESTAMPTZ NOT NULL,
    delivery_window_end     TIMESTAMPTZ NOT NULL,
    version                 BIGINT NOT NULL DEFAULT 0,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_orders_org ON orders (organization_id);

-- Vehicles
CREATE TABLE vehicles (
    id                      VARCHAR(36) PRIMARY KEY,
    organization_id         VARCHAR(36) NOT NULL,
    license_plate           VARCHAR(20) NOT NULL,
    capacity_weight_kg      DOUBLE PRECISION NOT NULL,
    capacity_volume_liters  DOUBLE PRECISION NOT NULL,
    driver_id               VARCHAR(36) NOT NULL,
    driver_name             VARCHAR(255) NOT NULL,
    driver_license          VARCHAR(50) NOT NULL,
    work_start              TIME NOT NULL,
    work_end                TIME NOT NULL,
    version                 BIGINT NOT NULL DEFAULT 0,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_vehicles_org ON vehicles (organization_id);

-- Jobs
CREATE TABLE jobs (
    id              VARCHAR(36) PRIMARY KEY,
    organization_id VARCHAR(36) NOT NULL,
    status          VARCHAR(20) NOT NULL,
    submitted_at    TIMESTAMPTZ NOT NULL,
    completed_at    TIMESTAMPTZ,
    error_message   TEXT,
    version         BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_jobs_org ON jobs (organization_id);
CREATE INDEX idx_jobs_status ON jobs (status);

-- Trips
CREATE TABLE trips (
    id               VARCHAR(36) PRIMARY KEY,
    job_id           VARCHAR(36) NOT NULL REFERENCES jobs(id),
    organization_id  VARCHAR(36) NOT NULL,
    vehicle_id       VARCHAR(36) NOT NULL,
    total_distance_km DOUBLE PRECISION NOT NULL DEFAULT 0,
    version          BIGINT NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_trips_job ON trips (job_id);
CREATE INDEX idx_trips_org ON trips (organization_id);
