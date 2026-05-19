-- Create indexes for performance

-- Orders indexes
CREATE INDEX idx_orders_organization_id ON orders(organization_id);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_orders_time_window ON orders(time_window_start, time_window_end);

-- Line items indexes
CREATE INDEX idx_line_items_order_id ON line_items(order_id);

-- Vehicles indexes
CREATE INDEX idx_vehicles_organization_id ON vehicles(organization_id);
CREATE INDEX idx_vehicles_created_at ON vehicles(created_at);

-- VRP jobs indexes
CREATE INDEX idx_vrp_jobs_organization_id ON vrp_jobs(organization_id);
CREATE INDEX idx_vrp_jobs_status ON vrp_jobs(status);
CREATE INDEX idx_vrp_jobs_created_at ON vrp_jobs(created_at);
CREATE INDEX idx_vrp_jobs_org_status ON vrp_jobs(organization_id, status);

-- Job orders indexes
CREATE INDEX idx_job_orders_order_id ON job_orders(order_id);

-- Job vehicles indexes
CREATE INDEX idx_job_vehicles_vehicle_id ON job_vehicles(vehicle_id);

-- Trips indexes
CREATE INDEX idx_trips_organization_id ON trips(organization_id);
CREATE INDEX idx_trips_job_id ON trips(job_id);
CREATE INDEX idx_trips_vehicle_id ON trips(vehicle_id);
CREATE INDEX idx_trips_created_at ON trips(created_at);

-- Visits indexes
CREATE INDEX idx_visits_trip_id ON visits(trip_id);
CREATE INDEX idx_visits_order_id ON visits(order_id);
CREATE INDEX idx_visits_sequence ON visits(trip_id, sequence_number);
