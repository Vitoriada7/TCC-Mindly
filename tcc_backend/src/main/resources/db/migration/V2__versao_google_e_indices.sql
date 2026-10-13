ALTER TABLE conexoes_google ADD COLUMN IF NOT EXISTS versao BIGINT;
UPDATE conexoes_google SET versao = 0 WHERE versao IS NULL;
ALTER TABLE conexoes_google ALTER COLUMN versao SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_categorias_usuario ON categorias(usuario_id);
CREATE INDEX IF NOT EXISTS idx_tarefas_usuario ON tarefas(usuario_id);
CREATE INDEX IF NOT EXISTS idx_tarefas_categoria ON tarefas(categoria_id);
CREATE INDEX IF NOT EXISTS idx_habitos_usuario ON habitos(usuario_id);
CREATE INDEX IF NOT EXISTS idx_contatos_usuario ON contatos_emergencia(usuario_id);
