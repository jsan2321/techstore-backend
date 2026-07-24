package com.ecoapi.techstore.user.infrastructure.adapter.input.rest;

import com.ecoapi.techstore.common.infrastructure.security.util.SecurityContextUtil;
import com.ecoapi.techstore.user.application.port.in.CreateAddressUseCase;
import com.ecoapi.techstore.user.application.port.in.DeleteAddressUseCase;
import com.ecoapi.techstore.user.application.port.in.GetAddressUseCase;
import com.ecoapi.techstore.user.application.port.in.ListAddressesUseCase;
import com.ecoapi.techstore.user.application.port.in.SetDefaultAddressUseCase;
import com.ecoapi.techstore.user.application.port.in.UpdateAddressUseCase;
import com.ecoapi.techstore.user.application.service.dto.CreateAddressCommand;
import com.ecoapi.techstore.user.application.service.dto.DeleteAddressCommand;
import com.ecoapi.techstore.user.application.service.dto.GetAddressQuery;
import com.ecoapi.techstore.user.application.service.dto.ListAddressesQuery;
import com.ecoapi.techstore.user.application.service.dto.SetDefaultAddressCommand;
import com.ecoapi.techstore.user.application.service.dto.UpdateAddressCommand;
import com.ecoapi.techstore.user.domain.model.SavedAddress;
import com.ecoapi.techstore.user.infrastructure.adapter.input.rest.request.CreateAddressRequest;
import com.ecoapi.techstore.user.infrastructure.adapter.input.rest.request.UpdateAddressRequest;
import com.ecoapi.techstore.user.infrastructure.adapter.input.rest.response.SavedAddressResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for self-service management of user saved addresses.
 */
@RestController
@RequestMapping("${api.prefix}/users/me/addresses")
@Tag(name = "Users - Addresses", description = "Saved address management endpoints for authenticated users")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")
public class UserAddressController {

    private final CreateAddressUseCase createAddressUseCase;
    private final ListAddressesUseCase listAddressesUseCase;
    private final GetAddressUseCase getAddressUseCase;
    private final UpdateAddressUseCase updateAddressUseCase;
    private final DeleteAddressUseCase deleteAddressUseCase;
    private final SetDefaultAddressUseCase setDefaultAddressUseCase;

    public UserAddressController(CreateAddressUseCase createAddressUseCase,
                                 ListAddressesUseCase listAddressesUseCase,
                                 GetAddressUseCase getAddressUseCase,
                                 UpdateAddressUseCase updateAddressUseCase,
                                 DeleteAddressUseCase deleteAddressUseCase,
                                 SetDefaultAddressUseCase setDefaultAddressUseCase) {
        this.createAddressUseCase = createAddressUseCase;
        this.listAddressesUseCase = listAddressesUseCase;
        this.getAddressUseCase = getAddressUseCase;
        this.updateAddressUseCase = updateAddressUseCase;
        this.deleteAddressUseCase = deleteAddressUseCase;
        this.setDefaultAddressUseCase = setDefaultAddressUseCase;
    }

    @Operation(summary = "List my saved addresses")
    @ApiResponse(responseCode = "200", description = "Addresses retrieved successfully")
    @GetMapping
    public ResponseEntity<List<SavedAddressResponse>> listMyAddresses() {
        Long userId = getCurrentUserId();

        List<SavedAddressResponse> response = listAddressesUseCase.execute(new ListAddressesQuery(userId))
                .stream()
                .map(SavedAddressResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get one of my saved addresses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SavedAddressResponse.class))),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @GetMapping("/{addressId}")
    public ResponseEntity<SavedAddressResponse> getMyAddress(
            @Parameter(description = "Address ID") @PathVariable Long addressId) {
        Long userId = getCurrentUserId();
        SavedAddress address = getAddressUseCase.execute(new GetAddressQuery(userId, addressId));
        return ResponseEntity.ok(SavedAddressResponse.from(address));
    }

    @Operation(summary = "Create a new saved address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Address created successfully",
                    content = @Content(schema = @Schema(implementation = SavedAddressResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<SavedAddressResponse> createAddress(@Valid @RequestBody CreateAddressRequest request) {
        Long userId = getCurrentUserId();

        CreateAddressCommand command = new CreateAddressCommand(
                userId,
                request.label(),
                request.recipientName(),
                request.street(),
                request.addressLine2(),
                request.city(),
                request.state(),
                request.zipCode(),
                request.country(),
                request.type(),
                request.isDefault()
        );

        SavedAddress savedAddress = createAddressUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(SavedAddressResponse.from(savedAddress));
    }

    @Operation(summary = "Update one of my saved addresses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address updated successfully",
                    content = @Content(schema = @Schema(implementation = SavedAddressResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @PutMapping("/{addressId}")
    public ResponseEntity<SavedAddressResponse> updateAddress(
            @Parameter(description = "Address ID") @PathVariable Long addressId,
            @Valid @RequestBody UpdateAddressRequest request) {
        Long userId = getCurrentUserId();

        UpdateAddressCommand command = new UpdateAddressCommand(
                userId,
                addressId,
                request.label(),
                request.recipientName(),
                request.street(),
                request.addressLine2(),
                request.city(),
                request.state(),
                request.zipCode(),
                request.country(),
                request.type()
        );

        SavedAddress updatedAddress = updateAddressUseCase.execute(command);
        return ResponseEntity.ok(SavedAddressResponse.from(updatedAddress));
    }

    @Operation(summary = "Delete one of my saved addresses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Address deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @Parameter(description = "Address ID") @PathVariable Long addressId) {
        Long userId = getCurrentUserId();
        deleteAddressUseCase.execute(new DeleteAddressCommand(userId, addressId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Set one of my saved addresses as default")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Default address updated successfully",
                    content = @Content(schema = @Schema(implementation = SavedAddressResponse.class))),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @PutMapping("/{addressId}/default")
    public ResponseEntity<SavedAddressResponse> setDefaultAddress(
            @Parameter(description = "Address ID") @PathVariable Long addressId) {
        Long userId = getCurrentUserId();
        SavedAddress savedAddress = setDefaultAddressUseCase.execute(
                new SetDefaultAddressCommand(userId, addressId)
        );
        return ResponseEntity.ok(SavedAddressResponse.from(savedAddress));
    }

    private Long getCurrentUserId() {
        return SecurityContextUtil.getCurrentUserId()
                .orElseThrow(() -> new SecurityException("No authenticated user"));
    }
}
