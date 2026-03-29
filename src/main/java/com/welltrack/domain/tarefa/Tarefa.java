package com.welltrack.domain.tarefa;

import com.welltrack.domain.lista.Lista;
import com.welltrack.domain.tarefabloco.TarefaBloco;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Tarefa")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idTarefa")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_tarefa")
    private UUID idTarefa;

    @Column(nullable = false, length = 100)
    private String descricao;

    @Column(nullable = false, length = 50)
    private String status = "PENDENTE";

    private LocalDateTime prazoMaximo;

    @Column(length = 500)
    private String imagemTarefa;

    @ManyToOne
    @JoinColumn(name = "id_lista", nullable = false)
    private Lista lista;

    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL)
    private List<TarefaBloco> blocos;

    public void atualizar(com.welltrack.dto.tarefa.DadosAtualizacaoTarefa dados) {
        if (dados.descricao() != null) {
            this.descricao = dados.descricao();
        }

        if (dados.status() != null) {
            this.status = dados.status();
        }

        if (dados.prazoMaximo() != null) {
            this.prazoMaximo = dados.prazoMaximo();
        }

        if (dados.imagemTarefa() != null) {
            this.imagemTarefa = dados.imagemTarefa();
        }
    }
}
