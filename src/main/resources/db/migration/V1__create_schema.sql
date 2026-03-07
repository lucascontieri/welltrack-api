-- =====================================================
-- CRIAÇÃO DOS TIPOS ENUM
-- =====================================================
CREATE TYPE tipo_usuario AS ENUM ('admin', 'usuario');
CREATE TYPE status_tarefa AS ENUM ('pendente', 'em andamento', 'concluida', 'cancelada');
CREATE TYPE tipo_bloco AS ENUM ('texto', 'imagem', 'checklist', 'anotacao');
CREATE TYPE unidade_medida AS ENUM ('g', 'kg', 'ml', 'l', 'unidade', 'xicara', 'colher');

-- =====================================================
-- TABELA USUARIO
-- =====================================================
CREATE TABLE Usuario (
                         id_usuario UUID PRIMARY KEY,
                         nome VARCHAR(70) NOT NULL,
                         cpf VARCHAR(11) UNIQUE,
                         tipoUsuario tipo_usuario NOT NULL,
                         dataNascimento DATE NOT NULL,
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
                         imagemUsuario VARCHAR(500)
);

-- =====================================================
-- TABELA TOKEN RECUPERACAO ✅ ADICIONADA
-- =====================================================
CREATE TABLE TokenRecuperacao (
                                  id_token UUID PRIMARY KEY,
                                  token VARCHAR(255) NOT NULL,
                                  expiracao TIMESTAMP NOT NULL,
                                  usado BOOLEAN DEFAULT FALSE,
                                  tentativas INTEGER DEFAULT 0,
                                  dataCriacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  id_usuario UUID NOT NULL,
                                  FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

-- =====================================================
-- TABELA GRUPO MUSCULAR
-- =====================================================
CREATE TABLE GrupoMuscular (
                               id_grupo UUID PRIMARY KEY,
                               nomeGrupoMuscular VARCHAR(50) NOT NULL
);

-- =====================================================
-- TABELA EXERCICIO
-- =====================================================
CREATE TABLE Exercicio (
                           id_exercicio UUID PRIMARY KEY,
                           nomeExercicio VARCHAR(50) NOT NULL,
                           imagemExercicio VARCHAR(500),
                           videoExercicio VARCHAR(500),
                           id_grupo UUID NOT NULL,
                           id_usuario UUID NOT NULL,
                           FOREIGN KEY (id_grupo) REFERENCES GrupoMuscular(id_grupo) ON DELETE CASCADE,
                           FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

-- =====================================================
-- TABELA TREINO ✅ CORRIGIDA
-- =====================================================
CREATE TABLE Treino (
                        id_treino UUID PRIMARY KEY,
                        nomeTreino VARCHAR(50) NOT NULL,
                        data DATE,
                        id_usuario UUID NOT NULL,
                        FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

-- =====================================================
-- TABELA TREINO EXERCICIO
-- =====================================================
CREATE TABLE TreinoExercicio (
                                 id_treino UUID NOT NULL,
                                 id_exercicio UUID NOT NULL,
                                 serieExercicio INTEGER,
                                 descanso INTEGER,
                                 carga DECIMAL(5,2),
                                 repeticoes INTEGER,
                                 ordem INTEGER,
                                 PRIMARY KEY (id_treino, id_exercicio),
                                 FOREIGN KEY (id_treino) REFERENCES Treino(id_treino) ON DELETE CASCADE,
                                 FOREIGN KEY (id_exercicio) REFERENCES Exercicio(id_exercicio) ON DELETE CASCADE
);

-- =====================================================
-- TABELA REGISTRO TREINO ✅ ADICIONADA
-- =====================================================
CREATE TABLE RegistroTreino (
                                id_registro UUID PRIMARY KEY,
                                dataExecucao DATE NOT NULL,
                                horaEntrada TIME,
                                horaSaida TIME,
                                concluido BOOLEAN DEFAULT FALSE,
                                id_usuario UUID NOT NULL,
                                id_treino UUID NOT NULL,
                                FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
                                FOREIGN KEY (id_treino) REFERENCES Treino(id_treino) ON DELETE CASCADE
);

-- =====================================================
-- TABELA ALIMENTO
-- =====================================================
CREATE TABLE Alimento (
                          id_alimento UUID PRIMARY KEY,
                          nomeAlimento VARCHAR(50) NOT NULL,
                          carboidrato DECIMAL(6,2),
                          proteina DECIMAL(6,2),
                          gordura DECIMAL(6,2),
                          unidadePadrao VARCHAR(10) NOT NULL,
                          pesoPorcao DECIMAL(7,2) NOT NULL,
                          calorias DECIMAL(6,2) NOT NULL,
                          imagemAlimento VARCHAR(500),
                          id_usuario UUID NOT NULL,
                          FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

-- =====================================================
-- TABELA REFEICAO
-- =====================================================
CREATE TABLE Refeicao (
                          id_refeicao UUID PRIMARY KEY,
                          nomeRefeicao VARCHAR(100) NOT NULL,
                          data DATE,
                          horario TIME,
                          dataRegistro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          imagemRefeicao VARCHAR(500),
                          id_usuario UUID NOT NULL,
                          FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

-- =====================================================
-- TABELA REFEICAO ALIMENTO
-- =====================================================
CREATE TABLE RefeicaoAlimento (
                                  id_refeicao UUID NOT NULL,
                                  id_alimento UUID NOT NULL,
                                  quantidade DECIMAL(7,2) NOT NULL,
                                  unidadeMedida unidade_medida NOT NULL,
                                  PRIMARY KEY (id_refeicao, id_alimento),
                                  FOREIGN KEY (id_refeicao) REFERENCES Refeicao(id_refeicao) ON DELETE CASCADE,
                                  FOREIGN KEY (id_alimento) REFERENCES Alimento(id_alimento) ON DELETE CASCADE
);

-- =====================================================
-- TABELA LISTA
-- =====================================================
CREATE TABLE Lista (
                       id_lista UUID PRIMARY KEY,
                       nomeLista VARCHAR(50) NOT NULL,
                       dataCriacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       id_usuario UUID NOT NULL,
                       FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

-- =====================================================
-- TABELA TAREFA
-- =====================================================
CREATE TABLE Tarefa (
                        id_tarefa UUID PRIMARY KEY,
                        descricao VARCHAR(100) NOT NULL,
                        status status_tarefa NOT NULL DEFAULT 'pendente',
                        prazoMaximo TIMESTAMP,
                        imagemTarefa VARCHAR(500),
                        id_lista UUID NOT NULL,
                        FOREIGN KEY (id_lista) REFERENCES Lista(id_lista) ON DELETE CASCADE
);

-- =====================================================
-- TABELA TAREFA BLOCO
-- =====================================================
CREATE TABLE TarefaBloco (
                             id_bloco UUID PRIMARY KEY,
                             tipo tipo_bloco NOT NULL,
                             conteudo TEXT NOT NULL,
                             ordem INTEGER NOT NULL,
                             propriedades JSONB,
                             id_pai UUID,
                             id_tarefa UUID NOT NULL,
                             FOREIGN KEY (id_pai) REFERENCES TarefaBloco(id_bloco) ON DELETE CASCADE,
                             FOREIGN KEY (id_tarefa) REFERENCES Tarefa(id_tarefa) ON DELETE CASCADE
);