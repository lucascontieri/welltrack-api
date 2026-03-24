package com.welltrack.domain.registrotreino;

import com.welltrack.domain.treino.Treino;
import com.welltrack.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity(name = "RegistroTreino")
@Table(name = "RegistroTreino")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idRegistroTreino")
public class RegistroTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_registro")
    private UUID idRegistro;

    @Column(nullable = false)
    private LocalDate dataExecucao;

    private LocalTime horaEntrada;

    private LocalTime horaSaida;

    @Column(nullable = false)
    private Boolean concluido = false;

    @ManyToOne
    @JoinColumn(name = "id_treino", nullable = false)
    private Treino treino;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}
