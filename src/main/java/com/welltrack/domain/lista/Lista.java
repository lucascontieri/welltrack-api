package com.welltrack.domain.lista;

import com.welltrack.domain.tarefa.Tarefa;
import com.welltrack.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Lista")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idLista")
public class Lista {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_lista")
    private UUID id_lista;

    @Column(nullable = false, length = 50)
    private String nomeLista;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "lista", cascade = CascadeType.ALL)
    private List<Tarefa> tarefas;
}
