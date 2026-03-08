-- =====================================================
-- TABELA USUARIO
-- =====================================================
CREATE TABLE usuario (
                         id_usuario UUID PRIMARY KEY,
                         nome VARCHAR(70) NOT NULL,
                         cpf VARCHAR(11) UNIQUE,
                         tipo_usuario VARCHAR(50) NOT NULL,
                         data_nascimento DATE NOT NULL,
                         peso DECIMAL(5,2),
                         altura DECIMAL(3,2),
                         email VARCHAR(50) NOT NULL UNIQUE,
                         senha VARCHAR(255) NOT NULL,
                         celular VARCHAR(20),
                         logradouro VARCHAR(50),
                         numero INTEGER,
                         complemento VARCHAR(50),
                         bairro VARCHAR(50),
                         cidade VARCHAR(50),
                         estado CHAR(2),
                         cep CHAR(8),
                         imagem_usuario VARCHAR(500)
);

-- =====================================================
-- TABELA TOKEN RECUPERACAO
-- =====================================================
CREATE TABLE token_recuperacao (
                                   id_token UUID PRIMARY KEY,
                                   token VARCHAR(255) NOT NULL,
                                   expiracao TIMESTAMP NOT NULL,
                                   usado BOOLEAN DEFAULT FALSE,
                                   tentativas INTEGER DEFAULT 0,
                                   data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   id_usuario UUID NOT NULL,
                                   FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

-- =====================================================
-- TABELA GRUPO MUSCULAR
-- =====================================================
CREATE TABLE grupo_muscular (
                                id_grupo UUID PRIMARY KEY,
                                nome_grupo_muscular VARCHAR(50) NOT NULL
);

-- =====================================================
-- TABELA EXERCICIO
-- =====================================================
CREATE TABLE exercicio (
                           id_exercicio UUID PRIMARY KEY,
                           nome_exercicio VARCHAR(50) NOT NULL,
                           imagem_exercicio VARCHAR(500),
                           video_exercicio VARCHAR(500),
                           id_grupo UUID NOT NULL,
                           id_usuario UUID NOT NULL,
                           FOREIGN KEY (id_grupo) REFERENCES grupo_muscular(id_grupo) ON DELETE CASCADE,
                           FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

-- =====================================================
-- TABELA TREINO
-- =====================================================
CREATE TABLE treino (
                        id_treino UUID PRIMARY KEY,
                        nome_treino VARCHAR(50) NOT NULL,
                        data DATE,
                        id_usuario UUID NOT NULL,
                        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

-- =====================================================
-- TABELA TREINO EXERCICIO
-- =====================================================
CREATE TABLE treino_exercicio (
                                  id_treino UUID NOT NULL,
                                  id_exercicio UUID NOT NULL,
                                  serie_exercicio INTEGER,
                                  descanso INTEGER,
                                  carga DECIMAL(5,2),
                                  repeticoes INTEGER,
                                  ordem INTEGER,
                                  PRIMARY KEY (id_treino, id_exercicio),
                                  FOREIGN KEY (id_treino) REFERENCES treino(id_treino) ON DELETE CASCADE,
                                  FOREIGN KEY (id_exercicio) REFERENCES exercicio(id_exercicio) ON DELETE CASCADE
);

-- =====================================================
-- TABELA REGISTRO TREINO
-- =====================================================
CREATE TABLE registro_treino (
                                 id_registro UUID PRIMARY KEY,
                                 data_execucao DATE NOT NULL,
                                 hora_entrada TIME,
                                 hora_saida TIME,
                                 concluido BOOLEAN DEFAULT FALSE,
                                 id_usuario UUID NOT NULL,
                                 id_treino UUID NOT NULL,
                                 FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
                                 FOREIGN KEY (id_treino) REFERENCES treino(id_treino) ON DELETE CASCADE
);

-- =====================================================
-- TABELA ALIMENTO
-- =====================================================
CREATE TABLE alimento (
                          id_alimento UUID PRIMARY KEY,
                          nome_alimento VARCHAR(50) NOT NULL,
                          carboidrato DECIMAL(6,2),
                          proteina DECIMAL(6,2),
                          gordura DECIMAL(6,2),
                          unidade_padrao VARCHAR(10) NOT NULL,
                          peso_porcao DECIMAL(7,2) NOT NULL,
                          calorias DECIMAL(6,2) NOT NULL,
                          imagem_alimento VARCHAR(500),
                          id_usuario UUID NOT NULL,
                          FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

-- =====================================================
-- TABELA REFEICAO
-- =====================================================
CREATE TABLE refeicao (
                          id_refeicao UUID PRIMARY KEY,
                          nome_refeicao VARCHAR(100) NOT NULL,
                          data DATE,
                          horario TIME,
                          data_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          imagem_refeicao VARCHAR(500),
                          id_usuario UUID NOT NULL,
                          FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

-- =====================================================
-- TABELA REFEICAO ALIMENTO
-- =====================================================
CREATE TABLE refeicao_alimento (
                                   id_refeicao UUID NOT NULL,
                                   id_alimento UUID NOT NULL,
                                   quantidade DECIMAL(7,2) NOT NULL,
                                   unidade_medida VARCHAR(20) NOT NULL,
                                   PRIMARY KEY (id_refeicao, id_alimento),
                                   FOREIGN KEY (id_refeicao) REFERENCES refeicao(id_refeicao) ON DELETE CASCADE,
                                   FOREIGN KEY (id_alimento) REFERENCES alimento(id_alimento) ON DELETE CASCADE
);

-- =====================================================
-- TABELA LISTA
-- =====================================================
CREATE TABLE lista (
                       id_lista UUID PRIMARY KEY,
                       nome_lista VARCHAR(50) NOT NULL,
                       data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       id_usuario UUID NOT NULL,
                       FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

-- =====================================================
-- TABELA TAREFA
-- =====================================================
CREATE TABLE tarefa (
                        id_tarefa UUID PRIMARY KEY,
                        descricao VARCHAR(100) NOT NULL,
                        status VARCHAR(50) NOT NULL DEFAULT 'PENDENTE',
                        prazo_maximo TIMESTAMP,
                        imagem_tarefa VARCHAR(500),
                        id_lista UUID NOT NULL,
                        FOREIGN KEY (id_lista) REFERENCES lista(id_lista) ON DELETE CASCADE
);

-- =====================================================
-- TABELA TAREFA BLOCO
-- =====================================================
CREATE TABLE tarefa_bloco (
                              id_bloco UUID PRIMARY KEY,
                              tipo VARCHAR(50) NOT NULL,
                              conteudo TEXT NOT NULL,
                              ordem INTEGER NOT NULL,
                              propriedades JSONB,
                              id_pai UUID,
                              id_tarefa UUID NOT NULL,
                              FOREIGN KEY (id_pai) REFERENCES tarefa_bloco(id_bloco) ON DELETE CASCADE,
                              FOREIGN KEY (id_tarefa) REFERENCES tarefa(id_tarefa) ON DELETE CASCADE
);