CREATE TABLE tb_refresh_token (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    token UUID NOT NULL UNIQUE,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user FOREIGN KEY (user_id) references tb_user(id) ON DELETE CASCADE
);