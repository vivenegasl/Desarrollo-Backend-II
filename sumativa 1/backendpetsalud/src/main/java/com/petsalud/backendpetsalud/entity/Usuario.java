package com.petsalud.backendpetsalud.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // Proteccion de datos sensibles (Ley 19.628 / OWASP A02): nunca serializar contrasenas en respuestas JSON
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private String password;

    private String nombre;

    private String apellido;

    private String telefono;

    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // Relaciones bidireccionales ignoradas en serializacion para evitar recursion infinita y fugas de memoria
    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<Mascota> mascotas;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<Venta> ventas;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<Recompensa> recompensas;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<Notificacion> notificaciones;
}
