CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS app_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), 
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS app_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS app_user_roles (
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES app_roles(id) ON DELETE CASCADE,
    PRIMARY KEY(user_id, role_id)
);

CREATE TABLE IF NOT EXISTS app_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL,
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE
);

CREATE TABLE resource_types (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,   -- 'db', 'api', 'ssh', 'storage'
    description TEXT
);

CREATE TABLE resources (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    type_id INTEGER NOT NULL REFERENCES resource_types(id),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_by UUID REFERENCES app_users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE resource_db_details (
    resource_id UUID PRIMARY KEY REFERENCES resources(id) ON DELETE CASCADE,

    engine VARCHAR(50) NOT NULL,         --  mysql, mongo, etc.
    host VARCHAR(255) NOT NULL,
    port INTEGER, -- puede ser null
    db_name VARCHAR(255) NOT NULL,
    username VARCHAR(100) NOT NULL,
    encrypted_password TEXT NOT NULL,
    params TEXT, -- resto de la url en formato json o null

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS access_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    resource_id UUID NOT NULL REFERENCES resources(id) ON DELETE CASCADE,
    reason TEXT,
    status TEXT NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')) DEFAULT 'PENDING',
    requested_hours INTEGER NOT NULL CHECK (requested_hours > 0),
    requested_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    reviewed_at TIMESTAMPTZ,
    reviewed_by UUID REFERENCES app_users(id) ON DELETE SET NULL
);

-- Seed
INSERT INTO app_roles (id, name) VALUES
    (gen_random_uuid(), 'ROLE_USER'),
    (gen_random_uuid(), 'ROLE_ADMIN');

INSERT INTO resource_types (code, description) VALUES
    ('db', 'Database'),
    ('api', 'API'),
    ('ssh', 'SSH');

