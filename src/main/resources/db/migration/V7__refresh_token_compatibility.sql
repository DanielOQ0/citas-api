-- El backend identifica la sesión por token_id; token_hash queda reservado
-- para instalaciones antiguas y no debe impedir guardar un refresh token.
ALTER TABLE refresh_tokens MODIFY COLUMN token_hash VARCHAR(255) NULL;
