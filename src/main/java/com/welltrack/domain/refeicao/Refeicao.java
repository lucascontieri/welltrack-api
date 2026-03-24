package com.welltrack.domain.refeicao;

import com.welltrack.domain.refeicaoalimento.RefeicaoAlimento;
import com.welltrack.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Refeicao")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idRefeicao")
public class Refeicao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_refeicao")
    private UUID idRefeicao;

    @Column(nullable = false, length = 100)
    private String nomeRefeicao;

    @Column(nullable = false)
    private LocalTime horario;

    @Column(nullable = false, length = 30)
    private String tipoRecorrencia;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String diasPersonalizados;

    @Column(length = 500)
    private String imagemRefeicao;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "refeicao", cascade = CascadeType.ALL)
    private List<RefeicaoAlimento> alimentos;
}
