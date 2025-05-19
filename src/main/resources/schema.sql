CREATE TABLE tbl_otz_user (
                              id UUID PRIMARY KEY,
                              email VARCHAR(100) NOT NULL UNIQUE,
                              password_hash VARCHAR(255) NOT NULL,
                              password_updated_at TIMESTAMP NOT NULL,
                              nickname VARCHAR(100) NOT NULL,
                              job VARCHAR(50) NOT NULL,
                              position VARCHAR(50) NOT NULL,
                              join_year VARCHAR(4),
                              marketing_opt_in BOOLEAN NOT NULL,
                              role VARCHAR(20) NOT NULL,
                              status VARCHAR(20) NOT NULL,
                              last_login_at TIMESTAMP,
                              last_failed_login_at TIMESTAMP,
                              profile_image_url TEXT,
                              created_at TIMESTAMP,
                              updated_at TIMESTAMP,
                              deleted_at TIMESTAMP
);

CREATE INDEX IDX_OTZ_USER_DELETED_AT ON tbl_otz_user (deleted_at);
