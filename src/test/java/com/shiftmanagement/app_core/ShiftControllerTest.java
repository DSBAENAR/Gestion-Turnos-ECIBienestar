package com.shiftmanagement.app_core;
import com.shiftmanagement.app_core.controllers.ShiftController;
import com.shiftmanagement.app_core.model.Shift;
import com.shiftmanagement.app_core.model.ShiftStatus;
import com.shiftmanagement.app_core.services.ShiftService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;






public class ShiftControllerTest {

    private ShiftService shiftService;
    private ShiftController shiftController;

    @BeforeEach
    void setUp() {
        shiftService = mock(ShiftService.class);
        shiftController = new ShiftController(shiftService);
    }

    @Test
    void testGetAllShifts_ReturnsShifts() {
        Shift shift = new Shift();
        when(shiftService.getShifts()).thenReturn(Flux.just(shift));

        StepVerifier.create(shiftController.getAllShifts())
                .expectNext(shift)
                .verifyComplete();
    }

    @Test
    void testGetAllShifts_Empty() {
        when(shiftService.getShifts()).thenReturn(Flux.empty());

        StepVerifier.create(shiftController.getAllShifts())
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void testPostShift_Success() {
        Shift shift = new Shift();
        when(shiftService.generateShift(shift)).thenReturn(Mono.empty());

        StepVerifier.create(shiftController.postShift(shift))
                .assertNext(response -> {
                    assertEquals(201, response.getStatusCodeValue());
                    assertEquals("Turno generado correctamente", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void testPostShift_Error() {
        Shift shift = new Shift();
        when(shiftService.generateShift(shift)).thenReturn(Mono.error(new RuntimeException("Error")));

        StepVerifier.create(shiftController.postShift(shift))
                .assertNext(response -> {
                    assertEquals(500, response.getStatusCodeValue());
                    assertEquals("Error", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void testDeleteShift_Success() {
        when(shiftService.deleteShift("1")).thenReturn(Mono.empty());

        StepVerifier.create(shiftController.deleteShift("1"))
                .assertNext(response -> {
                    assertEquals(200, response.getStatusCodeValue());
                    assertTrue(response.getBody().contains("deleted successfully"));
                })
                .verifyComplete();
    }

    @Test
    void testDeleteShift_NotFound() {
        when(shiftService.deleteShift("1")).thenReturn(Mono.error(new RuntimeException("Not found")));

        StepVerifier.create(shiftController.deleteShift("1"))
                .assertNext(response -> {
                    assertEquals(404, response.getStatusCodeValue());
                    assertEquals("Not found", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void testGetShiftsByRole() {
        Shift shift = new Shift();
        when(shiftService.getShiftsByRole("DOCTOR")).thenReturn(Flux.just(shift));

        StepVerifier.create(shiftController.getShiftsByRole("DOCTOR"))
                .expectNext(shift)
                .verifyComplete();
    }

    @Test
    void testGetShiftById_Success() {
        Shift shift = new Shift();
        when(shiftService.getShiftById("1")).thenReturn(Mono.just(shift));

        StepVerifier.create(shiftController.getShiftById("1"))
                .assertNext(response -> {
                    assertEquals(200, response.getStatusCodeValue());
                    assertEquals(shift, response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void testGetShiftById_Error() {
        when(shiftService.getShiftById("1")).thenReturn(Mono.error(new RuntimeException("Error")));

        StepVerifier.create(shiftController.getShiftById("1"))
                .assertNext(response -> {
                    assertEquals(500, response.getStatusCodeValue());
                    assertEquals(Collections.singletonMap("error", "Error"), response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void testGetShiftsByUserId() {
        Shift shift = new Shift();
        when(shiftService.getShiftsByUserId("user1")).thenReturn(Flux.just(shift));

        StepVerifier.create(shiftController.getShiftsByUserId("user1"))
                .expectNext(shift)
                .verifyComplete();
    }

    @Test
    void testDeleteTurnByCode_Success() {
        when(shiftService.deleteShiftByTurnCode("code1")).thenReturn(Mono.just("deleted"));

        StepVerifier.create(shiftController.deleteTurnByCode("code1"))
                .assertNext(response -> {
                    assertEquals(200, response.getStatusCodeValue());
                    assertEquals("deleted", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void testDeleteTurnByCode_Error() {
        when(shiftService.deleteShiftByTurnCode("code1")).thenReturn(Mono.error(new RuntimeException("Error")));

        StepVerifier.create(shiftController.deleteTurnByCode("code1"))
                .assertNext(response -> {
                    assertEquals(500, response.getStatusCodeValue());
                    assertEquals(Collections.singletonMap("error", "Error"), response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void testGetShiftByTurnCode_Success() {
        Shift shift = new Shift();
        when(shiftService.getShiftByTurnCode("code1")).thenReturn(Mono.just(shift));

        StepVerifier.create(shiftController.getShiftByTurnCode("code1"))
                .assertNext(response -> {
                    assertEquals(200, response.getStatusCodeValue());
                    assertEquals(shift, response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void testGetShiftByTurnCode_Error() {
        when(shiftService.getShiftByTurnCode("code1")).thenReturn(Mono.error(new RuntimeException("Error")));

        StepVerifier.create(shiftController.getShiftByTurnCode("code1"))
                .assertNext(response -> {
                    assertEquals(500, response.getStatusCodeValue());
                    assertEquals(Collections.singletonMap("error", "Error"), response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void testChangeShiftStatus_Success() {
        Shift shift = new Shift();
        ShiftStatus status = ShiftStatus.ASSIGNED;
        when(shiftService.getShiftByTurnCode("code1")).thenReturn(Mono.just(shift));
        when(shiftService.changeShiftStatus(shift, status)).thenReturn(Mono.just(shift));

        StepVerifier.create(shiftController.changeShiftStatus("code1", status))
                .assertNext(response -> {
                    assertEquals(200, response.getStatusCodeValue());
                    assertEquals(shift, response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void testChangeShiftStatus_Error() {
        ShiftStatus status = ShiftStatus.ASSIGNED;
        when(shiftService.getShiftByTurnCode("code1")).thenReturn(Mono.error(new RuntimeException("Error")));

        StepVerifier.create(shiftController.changeShiftStatus("code1", status))
                .assertNext(response -> {
                    assertEquals(500, response.getStatusCodeValue());
                    assertEquals(Collections.singletonMap("error", "Error"), response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void testGetShiftsBySpecialty() {
        Shift shift = new Shift();
        when(shiftService.getShiftsBySpecialty("cardiology")).thenReturn(Flux.just(shift));

        StepVerifier.create(shiftController.getShiftsBySpecialty("cardiology"))
                .expectNext(shift)
                .verifyComplete();
    }

    @Test
    void testGetShiftsByStatus() {
        Shift shift = new Shift();
        when(shiftService.getShiftsByStatus(ShiftStatus.ASSIGNED)).thenReturn(Flux.just(shift));

        StepVerifier.create(shiftController.getShiftsByStatus(ShiftStatus.ASSIGNED))
                .expectNext(shift)
                .verifyComplete();
    }

    @Test
    void testGetShiftsBySpecialPriority() {
        Shift shift = new Shift();
        when(shiftService.getShiftsBySpecialPriority(true)).thenReturn(Flux.just(shift));

        StepVerifier.create(shiftController.getShiftsBySpecialPriority(true))
                .expectNext(shift)
                .verifyComplete();
    }
}
