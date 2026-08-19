    package com.dilanmotos.application.UseCases;

    import com.dilanmotos.domain.model.Servicio;
    import com.dilanmotos.domain.repository.ServicioRepository;
    import com.dilanmotos.infrastructure.dto.ServicioRequestDTO;
    import com.dilanmotos.infrastructure.dto.ServicioResponseDTO;
    import com.dilanmotos.infrastructure.persistence.MecanicoEntity;
    import com.dilanmotos.infrastructure.persistence.MecanicoJpaRepository;
    import com.dilanmotos.infrastructure.persistence.ServicioEntity;
    import com.dilanmotos.infrastructure.persistence.TipoServicioEntity;
    import com.dilanmotos.infrastructure.persistence.TipoServicioJpaRepository;

    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;

    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;

    import java.sql.Date;
    import java.util.List;
    import java.util.Optional;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.*;

    @ExtendWith(MockitoExtension.class)
    class ServicioUCTest {

        private ServicioUC servicioUC;

        @Mock
        private ServicioRepository servicioRepository;
        @Mock
        private MecanicoJpaRepository mecanicoJpaRepository;
        @Mock
        private TipoServicioJpaRepository tipoServicioJpaRepository;

        @BeforeEach
        void setUp() {
            servicioUC = new ServicioUC(
                    servicioRepository,
                    mecanicoJpaRepository,
                    tipoServicioJpaRepository
            );
        }

        // CREAR SERVICIO
        @Test
        @DisplayName("Debe crear un servicio correctamente")
        void debeCrearServicioCorrectamente() {

            // Arrange
            ServicioRequestDTO request = new ServicioRequestDTO();

            request.setIdUsuario(1);
            request.setIdMecanico(2);
            request.setIdTipoServicio(3);
            request.setFechaServicio(Date.valueOf("2026-08-19"));
            request.setEstadoServicio("PENDIENTE");
            request.setComentario("Cambio de aceite");
            request.setPuntuacion(5);
            request.setVisibleEnHistorial(true);

            Servicio servicioGuardado = new Servicio();

            servicioGuardado.setIdServicio(1);
            servicioGuardado.setIdUsuario(1);
            servicioGuardado.setIdMecanico(2);
            servicioGuardado.setIdTipoServicio(3);
            servicioGuardado.setFechaServicio(Date.valueOf("2026-08-19"));
            servicioGuardado.setEstadoServicio("PENDIENTE");
            servicioGuardado.setComentario("Cambio de aceite");
            servicioGuardado.setPuntuacion(5);
            servicioGuardado.setVisibleEnHistorial(true);

            when(servicioRepository.guardar(any(Servicio.class)))
                    .thenReturn(servicioGuardado);

            // Act
            ServicioResponseDTO resultado = servicioUC.crear(request);

            // Assert
            assertNotNull(resultado);

            assertEquals(1, resultado.getIdServicio());
            assertEquals(1, resultado.getIdUsuario());
            assertEquals(2, resultado.getIdMecanico());
            assertEquals(3, resultado.getIdTipoServicio());
            assertEquals("PENDIENTE", resultado.getEstadoServicio());
            assertEquals("Cambio de aceite", resultado.getComentario());
            assertEquals(5, resultado.getPuntuacion());
            assertTrue(resultado.getVisibleEnHistorial());

            verify(servicioRepository, times(1))
                    .guardar(any(Servicio.class));
        }

        // LISTAR TODOS LOS SERVICIOS
        @Test
        @DisplayName("Debe listar todos los servicios correctamente")
        void debeListarTodosServiciosCorrectamente() {

            // Arrange
            Servicio servicio1 = new Servicio();

            servicio1.setIdServicio(1);
            servicio1.setIdUsuario(1);
            servicio1.setEstadoServicio("PENDIENTE");

            Servicio servicio2 = new Servicio();

            servicio2.setIdServicio(2);
            servicio2.setIdUsuario(2);
            servicio2.setEstadoServicio("FINALIZADO");

            List<Servicio> servicios = List.of(
                    servicio1,
                    servicio2
            );

            when(servicioRepository.obtenerTodas())
                    .thenReturn(servicios);

            // Act
            List<ServicioResponseDTO> resultado =
                    servicioUC.listarTodas();

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            assertEquals(1,resultado.get(0).getIdServicio());
            assertEquals("PENDIENTE",resultado.get(0).getEstadoServicio());
            assertEquals(2,resultado.get(1).getIdServicio());
            assertEquals("FINALIZADO",resultado.get(1).getEstadoServicio());
            verify(servicioRepository, times(1))
                    .obtenerTodas();
        }

        // LISTAR SERVICIOS POR USUARIO
        @Test
        @DisplayName("Debe listar los servicios de un usuario correctamente")
        void debeListarServiciosPorUsuarioCorrectamente() {

            // Arrange
            Integer idUsuario = 1;
            Integer idMecanico = 2;
            Integer idTipoServicio = 3;

            ServicioEntity servicioEntity =
                    new ServicioEntity();

            servicioEntity.setIdServicio(10);
            servicioEntity.setIdUsuario(idUsuario);
            servicioEntity.setIdMecanico(idMecanico);
            servicioEntity.setIdTipoServicio(idTipoServicio);
            servicioEntity.setFechaServicio(
                    Date.valueOf("2026-08-19")
            );
            servicioEntity.setEstadoServicio("PENDIENTE");
            servicioEntity.setComentario("Cambio de aceite");
            servicioEntity.setPuntuacion(5);
            servicioEntity.setVisibleEnHistorial(true);

            MecanicoEntity mecanico =
                    new MecanicoEntity();

            mecanico.setIdMecanico(idMecanico);
            mecanico.setNombre("Carlos Perez");
            mecanico.setEspecialidad("Motor");
            mecanico.setTelefono("3001234567");

            TipoServicioEntity tipoServicio =
                    new TipoServicioEntity();

            tipoServicio.setIdTipoServicio(idTipoServicio);
            tipoServicio.setNombre("Cambio de aceite");
            tipoServicio.setDescripcion(
                    "Cambio de aceite y revisión general"
            );

            when(servicioRepository.findByIdUsuario(idUsuario))
                    .thenReturn(List.of(servicioEntity));

            when(mecanicoJpaRepository.findById(idMecanico))
                    .thenReturn(Optional.of(mecanico));

            when(tipoServicioJpaRepository.findById(idTipoServicio))
                    .thenReturn(Optional.of(tipoServicio));

            // Act
            List<ServicioResponseDTO> resultado =
                    servicioUC.listarPorUsuario(idUsuario);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());

            ServicioResponseDTO servicio =
                    resultado.get(0);

            assertEquals(10,servicio.getIdServicio());
            assertEquals(idUsuario,servicio.getIdUsuario());
            assertEquals(idMecanico,servicio.getIdMecanico());
            assertEquals("Carlos Perez",servicio.getNombreMecanico());
            assertEquals(idTipoServicio,servicio.getIdTipoServicio());
            assertEquals("Cambio de aceite",servicio.getNombreServicio());
            assertEquals("PENDIENTE",servicio.getEstadoServicio());
            assertEquals("Cambio de aceite",servicio.getComentario());

            verify(servicioRepository, times(1))
                    .findByIdUsuario(idUsuario);

            verify(mecanicoJpaRepository, times(1))
                    .findById(idMecanico);

            verify(tipoServicioJpaRepository, times(1))
                    .findById(idTipoServicio);
        }

        // OBTENER SERVICIO POR ID

        @Test
        @DisplayName("Debe obtener un servicio por ID correctamente")
        void debeObtenerServicioPorIdCorrectamente() {

            // Arrange
            Integer id = 1;
            Servicio servicio = new Servicio();

            servicio.setIdServicio(id);
            servicio.setIdUsuario(1);
            servicio.setEstadoServicio("PENDIENTE");
            servicio.setComentario("Cambio de aceite");

            when(servicioRepository.buscarPorId(id))
                    .thenReturn(Optional.of(servicio));

            // Act
            ServicioResponseDTO resultado =
                    servicioUC.obtenerPorId(id);

            // Assert
            assertNotNull(resultado);
            assertEquals(id,resultado.getIdServicio());
            assertEquals("PENDIENTE",resultado.getEstadoServicio());
            assertEquals("Cambio de aceite",resultado.getComentario());

            verify(servicioRepository, times(1))
                    .buscarPorId(id);
        }

        // ACTUALIZAR SERVICIO

        @Test
        @DisplayName("Debe actualizar un servicio correctamente")
        void debeActualizarServicioCorrectamente() {

            // Arrange
            Integer id = 1;

            Servicio servicioExistente =
                    new Servicio();

            servicioExistente.setIdServicio(id);
            servicioExistente.setEstadoServicio("PENDIENTE");

            ServicioRequestDTO request =
                    new ServicioRequestDTO();

            request.setIdUsuario(1);
            request.setIdMecanico(2);
            request.setIdTipoServicio(3);
            request.setFechaServicio(
                    Date.valueOf("2026-08-19")
            );
            request.setEstadoServicio("FINALIZADO");
            request.setComentario("Servicio completado");
            request.setPuntuacion(5);
            request.setVisibleEnHistorial(true);

            when(servicioRepository.buscarPorId(id))
                    .thenReturn(Optional.of(servicioExistente));

            when(servicioRepository.actualizar(
                    any(Servicio.class)
            )).thenAnswer(invocation -> {

                Servicio servicio =
                        invocation.getArgument(0);

                return servicio;
            });

            // Act
            ServicioResponseDTO resultado =
                    servicioUC.actualizar(id, request);

            // Assert
            assertNotNull(resultado);
            assertEquals(id,resultado.getIdServicio());
            assertEquals("FINALIZADO",resultado.getEstadoServicio());
            assertEquals("Servicio completado",resultado.getComentario());
            assertEquals(5,resultado.getPuntuacion());

            verify(servicioRepository, times(1))
                    .buscarPorId(id);

            verify(servicioRepository, times(1))
                    .actualizar(any(Servicio.class));
        }

        // ELIMINAR SERVICIO
        @Test
        @DisplayName("Debe eliminar un servicio correctamente")
        void debeEliminarServicioCorrectamente() {

        // Arrange
        Integer id = 1;
        Servicio servicio = new Servicio();
        servicio.setIdServicio(id);

        when(servicioRepository.buscarPorId(id))
                .thenReturn(Optional.of(servicio));

        doNothing()
                .when(servicioRepository)
                .eliminar(id);

        // Act
        servicioUC.eliminar(id);

        // Assert
        verify(servicioRepository, times(1))
                .buscarPorId(id);

        verify(servicioRepository, times(1))
                .eliminar(id);
        }
    }