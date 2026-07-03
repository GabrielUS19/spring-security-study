CREATE TABLE tb_user (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL
);

CREATE TABLE tb_role (
    id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE tb_user_role (
    user_id UUID NOT NULL,
    role_id INTEGER NOT NULL,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES tb_role(id) ON DELETE CASCADE
);

INSERT INTO tb_role (name) VALUES ('ROLE_USER');
INSERT INTO tb_role (name) VALUES ('ROLE_ADMIN');