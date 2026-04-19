-- E-mail verificado (cadastro por senha fica false até confirmar; usuários antigos: true)
ALTER TABLE usuario
    ADD COLUMN email_verificado BOOLEAN NOT NULL DEFAULT TRUE;

UPDATE usuario SET email_verificado = TRUE;

-- Tokens de recuperação de senha e verificação de e-mail na mesma tabela (coluna tipo)
ALTER TABLE token_recuperacao
    ADD COLUMN IF NOT EXISTS tipo VARCHAR(30) NOT NULL DEFAULT 'RECUPERACAO_SENHA';

-- Remove tabela legada se existir (versões antigas do projeto)
DROP TABLE IF EXISTS email_verificacao;

CREATE UNIQUE INDEX IF NOT EXISTS uk_token_recuperacao_token ON token_recuperacao (token);
