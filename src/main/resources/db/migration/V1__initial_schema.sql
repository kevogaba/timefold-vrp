-- Initial schema for VRP system

-- Orders table
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(50),
    customer_email VARCHAR(255),
    customer_lat DOUBLE PRECISION NOT NULL,
    customer_lon DOUBLE PRECISION NOT NULL,
    pickup_lat DOUBLE PRECISION,
    pickup_lon DOUBLE PRECISION,
    delivery_lat DOUBLE PRECISION NOT NULL,
    delivery_lon DOUBLE PRECISION NOT NULL,
    time_window_start TIMESTAMP NOT NULL,
    time_window_end TIMESTAMP NOT NULL,
    service_duration_minutes INTEGER NOT NULL,
    priority INTEGER NOT NULL DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Line items table
CREATE TABLE line_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    weight DECIMAL(10,2) NOT NULL,
    volume DECIMAL(10,2) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Vehicles table
CREATE TABLE vehicles (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    license_plate VARCHAR(50) NOT NULL,
    weight_capacity DECIMAL(10,2) NOT NULL,
    volume_capacity DECIMAL(10,2) NOT NULL,
    start_lat DOUBLE PRECISION NOT NULL,
    start_lon DOUBLE PRECISION NOT NULL,
    end_lat DOUBLE PRECISION NOT NULL,
    end_lon DOUBLE PRECISION NOT NULL,
    available_from TIME NOT NULL,
    available_until TIME NOT NULL,
    driver_id UUID,
    driver_name VARCHAR(255),
    driver_license VARCHAR(100),
    driver_phone VARCHAR(50),
    driver_email VARCHAR(255),
    cost_per_km DECIMAL(10,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- VRP jobs table
CREATE TABLE vrp_jobs (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    hard_score INTEGER,
    soft_score INTEGER,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Job orders junction table
CREATE TABLE job_orders (
    job_id UUID NOT NULL,
    order_id UUID NOT NULL,
    PRIMARY KEY (job_id, order_id),
    FOREIGN KEY (job_id) REFERENCES vrp_jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Job vehicles junction table
CREATE TABLE job_vehicles (
    job_id UUID NOT NULL,
    vehicle_id UUID NOT NULL,
    PRIMARY KEY (job_id, vehicle_id),
    FOREIGN KEY (job_id) REFERENCES vrp_jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE
);

-- Trips table
CREATE TABLE trips (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    job_id UUID NOT NULL,
    vehicle_id UUID NOT NULL,
    total_distance_meters BIGINT NOT NULL,
    total_duration_minutes INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_id) REFERENCES vrp_jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

-- Visits table
CREATE TABLE visits (
    id UUID PRIMARY KEY,
    trip_id UUID NOT NULL,
    order_id UUID NOT NULL,
    location_lat DOUBLE PRECISION NOT NULL,
    location_lon DOUBLE PRECISION NOT NULL,
    arrival_time TIMESTAMP NOT NULL,
    departure_time TIMESTAMP NOT NULL,
    sequence_number INTEGER NOT NULL,
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);
