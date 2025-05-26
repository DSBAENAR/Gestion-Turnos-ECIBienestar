package com.shiftmanagement.app_core;

import com.shiftmanagement.app_core.model.*;
import com.shiftmanagement.app_core.repository.ShiftRepository;
import com.shiftmanagement.app_core.services.ShiftService;
import com.shiftmanagement.app_core.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ShiftServiceTest {

    private ShiftRepository shiftRepository;
    private UserService userService;
    private ShiftService shiftService;

    @BeforeEach
    void setUp() {
        shiftRepository = Mockito.mock(ShiftRepository.class);
        userService = Mockito.mock(UserService.class);
        shiftService = new ShiftService(shiftRepository, userService);
    }

    @Test
    void deleteShiftByTurnCode_shouldDeleteAndReturnTurnCode() {
        String turnCode = "PS-1";
        Shift mockShift = new Shift("123", "Psicologia", false);
        mockShift.setTurnCode(turnCode);

        when(shiftRepository.findByTurnCodeAndCreatedAtBetween(eq(turnCode), any(), any()))
            .thenReturn(Mono.just(mockShift));
        when(shiftRepository.delete(mockShift)).thenReturn(Mono.empty());

        StepVerifier.create(shiftService.deleteShiftByTurnCode(turnCode))
            .expectNext(turnCode)
            .verifyComplete();
    }

    @Test
    void getShiftsByUserId_shouldReturnShiftsOfUser() {
        String userId = "123";
        Shift s = new Shift(userId, "Psicologia", false);

        when(shiftRepository.findByUserId(userId)).thenReturn(Flux.just(s));

        StepVerifier.create(shiftService.getShiftsByUserId(userId))
            .expectNext(s)
            .verifyComplete();
    }

    @Test
    void getShiftById_shouldReturnMatchingShift() {
        String id = "shift123";
        Shift s = new Shift("123", "Psicologia", false);
        s.setId(id);

        when(shiftRepository.findById(id)).thenReturn(Mono.just(s));

        StepVerifier.create(shiftService.getShiftById(id))
            .expectNext(s)
            .verifyComplete();
    }


    @Test
    void getShiftsByRole_shouldReturnShiftsWithRole() {
        String role = "DOCTOR";
        Shift s = new Shift("123", "Medicina General", false);
        s.setUserRole(role);

        when(shiftRepository.findByUserRole(role)).thenReturn(Flux.just(s));

        StepVerifier.create(shiftService.getShiftsByRole(role))
            .expectNext(s)
            .verifyComplete();
    }


    @Test
    void getShiftsByStatus_shouldReturnShiftsWithStatus() {
        Shift s1 = new Shift("1", "Medicina General", false);
        s1.setStatus(ShiftStatus.ASSIGNED);

        when(shiftRepository.findByStatusAndCreatedAtBetween(eq(ShiftStatus.ASSIGNED), any(), any()))
            .thenReturn(Flux.just(s1));

        StepVerifier.create(shiftService.getShiftsByStatus(ShiftStatus.ASSIGNED))
            .expectNext(s1)
            .verifyComplete();
    }

    @Test
    void getShiftByTurnCode_shouldReturnMatchingShift() {
        String code = "MG-1";
        Shift s = new Shift("123", "Medicina General", false);
        s.setTurnCode(code);

        when(shiftRepository.findByTurnCodeAndCreatedAtBetween(eq(code), any(), any()))
            .thenReturn(Mono.just(s));

        StepVerifier.create(shiftService.getShiftByTurnCode(code))
            .expectNext(s)
            .verifyComplete();
    }


    @Test
    void getShiftsBySpecialPriority_shouldReturnMatchingShifts() {
        boolean priority = true;
        Shift s = new Shift("123", "Psicologia", priority);

        when(shiftRepository.findBySpecialPriorityAndCreatedAtBetween(eq(priority), any(), any()))
            .thenReturn(Flux.just(s));

        StepVerifier.create(shiftService.getShiftsBySpecialPriority(priority))
            .expectNext(s)
            .verifyComplete();
    }
    @Test
    void generateShift_shouldGenerateCorrectTurnCodeAndSaveShift() {
        // Arrange
        Shift shift = new Shift("123", "Psicologia", false);
        User mockUser = new User("John Doe", "123", "DOCTOR", null);

        Mockito.when(userService.getUserbyId("123"))
            .thenReturn(Mono.just(mockUser));

        Mockito.when(shiftRepository.countBySpecialtyAndCreatedAtBetween(any(), any(), any()))
            .thenReturn(Mono.just(5L));

        Mockito.when(shiftRepository.insert(Mockito.<Shift>any()))
            .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act
        StepVerifier.create(shiftService.generateShift(shift))
            .verifyComplete();

        // Assert: capturar el Shift que fue insertado
        ArgumentCaptor<Shift> captor = ArgumentCaptor.forClass(Shift.class);
        Mockito.verify(shiftRepository).insert(captor.capture());
        Shift inserted = captor.getValue();

        System.out.println("DEBUG: Shift insertado → " + inserted);

        assertEquals("123", inserted.getUserId());
        assertEquals("John Doe", inserted.getUsername());
        assertEquals("DOCTOR", inserted.getUserRole());
        assertEquals("Psicologia", inserted.getSpecialty());
        assertTrue(inserted.getTurnCode().startsWith("PS-"));
        assertEquals(ShiftStatus.ASSIGNED, inserted.getStatus());
        assertNotNull(inserted.getCreatedAt());
    }

    @Test
    void getShiftsBySpecialty_shouldReturnShifts() {
        String specialty = "Psicologia";
        Shift s = new Shift("123", specialty, false);

        when(shiftRepository.findBySpecialtyAndCreatedAtBetween(eq(specialty), any(), any()))
            .thenReturn(Flux.just(s));

        StepVerifier.create(shiftService.getShiftsBySpecialty(specialty))
            .expectNext(s)
            .verifyComplete();
    }

    @Test
    void getShifts_shouldReturnAllShifts() {
        Shift shift1 = new Shift(); shift1.setTurnCode("PS-1");
        Shift shift2 = new Shift(); shift2.setTurnCode("MG-2");

        Mockito.when(shiftRepository.findAll()).thenReturn(Flux.just(shift1, shift2));

        StepVerifier.create(shiftService.getShifts())
            .expectNext(shift1)
            .expectNext(shift2)
            .verifyComplete();
    }

    @Test
    void deleteShift_shouldDeleteIfExists() {
        Shift shift = new Shift(); shift.setId("shift123");

        Mockito.when(shiftRepository.findById("shift123")).thenReturn(Mono.just(shift));
        Mockito.when(shiftRepository.deleteById("shift123")).thenReturn(Mono.empty());

        StepVerifier.create(shiftService.deleteShift("shift123"))
            .verifyComplete();
    }

    @Test
    void deleteShift_shouldErrorIfNotFound() {
        Mockito.when(shiftRepository.findById("notFound"))
            .thenReturn(Mono.empty());

        StepVerifier.create(shiftService.deleteShift("notFound"))
            .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                                     e.getMessage().equals("No shift found with ID: notFound"))
            .verify();
    }

    @Test
    void changeShiftStatus_shouldUpdateStatusIfValid() {
        Shift shift = new Shift();
        shift.setStatus(ShiftStatus.ASSIGNED);

        Mockito.when(shiftRepository.save(any(Shift.class)))
            .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(shiftService.changeShiftStatus(shift, ShiftStatus.IN_PROGRESS))
            .expectNextMatches(updated -> updated.getStatus() == ShiftStatus.IN_PROGRESS)
            .verifyComplete();
    }

    @Test
    void changeShiftStatus_shouldErrorIfTransitionInvalid() {
        Shift shift = new Shift();
        shift.setStatus(ShiftStatus.ATTENDED);

        StepVerifier.create(shiftService.changeShiftStatus(shift, ShiftStatus.IN_PROGRESS))
            .expectErrorMatches(e -> e instanceof IllegalStateException)
            .verify();
    }
}
