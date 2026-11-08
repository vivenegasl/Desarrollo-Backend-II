package com.petsalud.backendpetsalud.config;

import com.petsalud.backendpetsalud.entity.*;
import com.petsalud.backendpetsalud.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Semillero de datos iniciales para pruebas locales y evaluacion docente inmediata.
 * Genera usuarios base con contrasenas cifradas con BCrypt y perfiles diferenciados:
 * - cliente@petsalud.cl  / Cliente123!  (CLIENTE)
 * - empleado@petsalud.cl / Empleado123! (EMPLEADO)
 * - gerente@petsalud.cl  / Gerente123!  (GERENTE)
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private ExpedienteMedicoRepository expedienteMedicoRepository;

    @Autowired
    private RecompensaRepository recompensaRepository;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            logger.info("La base de datos ya contiene registros. Omitiendo semillero.");
            return;
        }

        logger.info("Iniciando semillero de datos iniciales para Clinica PetSalud...");

        // 1. Usuarios con roles diferenciados y passwords cifradas con BCrypt
        Usuario cliente = Usuario.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .email("cliente@petsalud.cl")
                .password(passwordEncoder.encode("Cliente123!"))
                .telefono("+56911223344")
                .direccion("Av. Providencia 1234, Santiago")
                .rol(Rol.CLIENTE)
                .build();
        cliente = usuarioRepository.save(cliente);

        Usuario empleado = Usuario.builder()
                .nombre("Camila")
                .apellido("Morales")
                .email("empleado@petsalud.cl")
                .password(passwordEncoder.encode("Empleado123!"))
                .telefono("+56955667788")
                .direccion("Calle Los Aromos 45, Viña del Mar")
                .rol(Rol.EMPLEADO)
                .build();
        empleado = usuarioRepository.save(empleado);

        Usuario gerente = Usuario.builder()
                .nombre("Roberto")
                .apellido("Silva")
                .email("gerente@petsalud.cl")
                .password(passwordEncoder.encode("Gerente123!"))
                .telefono("+56999887766")
                .direccion("Av. Las Condes 7890, Santiago")
                .rol(Rol.GERENTE)
                .build();
        gerente = usuarioRepository.save(gerente);

        // 2. Mascotas
        Mascota max = Mascota.builder()
                .nombre("Max")
                .especie("Canino")
                .raza("Golden Retriever")
                .fechaNacimiento(LocalDate.of(2021, 5, 10))
                .sexo("Macho")
                .propietario(cliente)
                .build();
        max = mascotaRepository.save(max);

        Mascota luna = Mascota.builder()
                .nombre("Luna")
                .especie("Felino")
                .raza("Siamés")
                .fechaNacimiento(LocalDate.of(2022, 9, 15))
                .sexo("Hembra")
                .propietario(cliente)
                .build();
        luna = mascotaRepository.save(luna);

        // 3. Catalogo de Productos
        Producto p1 = Producto.builder()
                .nombre("Alimento Royal Canin Perro Adulto 15kg")
                .descripcion("Nutricion balanceada para razas medianas y grandes con acidos grasos Omega 3 y 6")
                .precio(new BigDecimal("65990"))
                .stock(25)
                .categoria("Alimentos")
                .build();
        productoRepository.save(p1);

        Producto p2 = Producto.builder()
                .nombre("Antiparasitario Nexgard Spectra 10-20kg")
                .descripcion("Tableta masticable saborizada contra pulgas, garrapatas y nematodos")
                .precio(new BigDecimal("22500"))
                .stock(40)
                .categoria("Farmacia")
                .build();
        productoRepository.save(p2);

        Producto p3 = Producto.builder()
                .nombre("Collar Reflectante Ajustable")
                .descripcion("Collar de nylon de alta visibilidad para paseos nocturnos seguros")
                .precio(new BigDecimal("7990"))
                .stock(18)
                .categoria("Accesorios")
                .build();
        productoRepository.save(p3);

        Producto p4 = Producto.builder()
                .nombre("Shampoo Hipoalergenico Avena 500ml")
                .descripcion("Formula suave para mascotas de piel sensible o alergias atopicas")
                .precio(new BigDecimal("12990"))
                .stock(3) // Stock bajo (< 5) para probar el endpoint de stock critico
                .categoria("Cuidado Estetico")
                .build();
        productoRepository.save(p4);

        // 4. Cita Medica
        Cita cita1 = Cita.builder()
                .fechaHora(LocalDateTime.now().plusDays(2).withHour(10).withMinute(30))
                .estado("Agendada")
                .motivo("Control general anual y administracion de vacuna antirrabica")
                .mascota(max)
                .veterinario(empleado)
                .build();
        citaRepository.save(cita1);

        // 5. Expediente Medico
        ExpedienteMedico expediente = ExpedienteMedico.builder()
                .fecha(LocalDateTime.now().minusMonths(3))
                .descripcion("Control preventivo y pesaje de rutina")
                .diagnostico("Paciente saludable, peso optimo (28.5 kg), sin signos clinicos anormales")
                .tratamiento("Se administra vacuna sextuple anual. Proximo control en 12 meses.")
                .mascota(max)
                .veterinario(empleado)
                .build();
        expedienteMedicoRepository.save(expediente);

        // 6. Recompensa de fidelizacion
        Recompensa recompensa = Recompensa.builder()
                .puntos(150)
                .descripcion("Bienvenida al Club PetSalud y bonificacion primera visita")
                .fecha(LocalDateTime.now())
                .usuario(cliente)
                .build();
        recompensaRepository.save(recompensa);

        // 7. Notificacion
        Notificacion notificacion = Notificacion.builder()
                .mensaje("Estimado Juan, recuerde la proxima cita de Max para su control anual.")
                .leida(false)
                .fecha(LocalDateTime.now())
                .usuario(cliente)
                .build();
        notificacionRepository.save(notificacion);

        logger.info("Semillero de datos iniciales completado exitosamente.");
    }
}
