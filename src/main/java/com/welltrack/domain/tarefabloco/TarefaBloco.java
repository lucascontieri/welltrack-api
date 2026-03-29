package com.welltrack.domain.tarefabloco;

import com.welltrack.domain.tarefa.Tarefa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "TarefaBloco")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idBloco")
public class TarefaBloco {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_bloco")
    private UUID idBloco;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Column(nullable = false)
    private Integer ordem;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String propriedades;

    @ManyToOne
    @JoinColumn(name = "id_pai")
    private TarefaBloco pai;

    @ManyToOne
    @JoinColumn(name = "id_tarefa", nullable = false)
    private Tarefa tarefa;

    public void atualizar(com.welltrack.dto.tarefabloco.DadosAtualizacaoTarefaBloco dados) {
        if (dados.tipo() != null) {
            this.tipo = dados.tipo();
        }

        if (dados.conteudo() != null) {
            this.conteudo = dados.conteudo();
        }

        if (dados.ordem() != null) {
            this.ordem = dados.ordem();
        }

        if (dados.propriedades() != null) {
            this.propriedades = dados.propriedades();
        }

        if (dados.idPai() != null) {
            this.pai = new TarefaBloco(dados.idPai(), null, null, null, null, null, null);
        }
    }
}
