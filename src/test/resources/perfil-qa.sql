-- Somente no banco descartável efficientia_perfil_qa, nunca em um banco compartilhado.
DO $$ BEGIN
    IF current_database() <> 'efficientia_perfil_qa' THEN
        RAISE EXCEPTION 'Este fixture exige o banco descartável efficientia_perfil_qa';
    END IF;
END $$;

CREATE TABLE empresa (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cnpj CHAR(14) UNIQUE,
    codigo VARCHAR(20) UNIQUE
);
CREATE TABLE adm (
    id SERIAL PRIMARY KEY,
    id_empresa INTEGER REFERENCES empresa(id),
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(254) UNIQUE NOT NULL,
    senha VARCHAR(100) NOT NULL
);
INSERT INTO empresa(id, nome, cnpj, codigo)
VALUES (1, 'Transportadora de teste', '00000000000000', 'QA-PERFIL');
INSERT INTO adm(id, id_empresa, nome, email, senha) VALUES
    (1, 1, 'Administrador', 'perfil@example.invalid', 'PerfilQA2026!'),
    (2, 1, 'Outra conta de teste', 'outra@example.invalid', 'PerfilQA2026!');
