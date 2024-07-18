//package ru.y_lab.swagger.API;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import ru.y_lab.dto.*;
//import ru.y_lab.swagger.shemas.AccessDeniedResponseSchema;
//import ru.y_lab.swagger.shemas.ForbiddenResponseSchema;
//import ru.y_lab.swagger.shemas.bookingAPI.BookingIllegalArgumentResponseSchema;
//import ru.y_lab.swagger.shemas.resourceAPI.ResourceNotFoundResponseSchema;
//
//import java.util.List;
//
//public interface BookingControllerAPI {
//
//    @Operation(summary = "Add a new booking",
//            description = "Creates a new booking based on the provided request",
//            security = @SecurityRequirement(name = "sessionAuth"))
//    @ApiResponses( value = {
//            @ApiResponse(responseCode = "201", description = "Booking created successfully",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = BookingDTO.class))),
//            @ApiResponse(responseCode = "400", description = "Invalid request data",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = BookingIllegalArgumentResponseSchema.class))),
//            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
//            @ApiResponse(responseCode = "403", description = "You do not have the necessary permissions to access this resource.",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ForbiddenResponseSchema.class)))
//    })
//    ResponseEntity<BookingDTO> addBooking(@RequestBody AddBookingRequestDTO request, HttpServletRequest httpRequest);
//
//    @Operation(summary = "Get booking by ID",
//            description = "Retrieves a booking by its ID",
//            security = @SecurityRequirement(name = "sessionAuth"))
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Booking found",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = BookingWithOwnerResourceDTO.class))),
//            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
//            @ApiResponse(responseCode = "404", description = "Booking not found",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ResourceNotFoundResponseSchema.class)))
//    })
//    ResponseEntity<BookingWithOwnerResourceDTO> getBookingById(@PathVariable Long bookingId, HttpServletRequest httpRequest);
//
//    @Operation(summary = "Get user bookings",
//            description = "Retrieves bookings for a specific user by their ID",
//            security = @SecurityRequirement(name = "sessionAuth"))
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Bookings found",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = BookingWithOwnerResourceDTO.class))),
//            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
//            @ApiResponse(responseCode = "403", description = "You do not have the necessary permissions to access this resource.",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ForbiddenResponseSchema.class))),
//            @ApiResponse(responseCode = "404", description = "User not found",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ResourceNotFoundResponseSchema.class)))
//    })
//    ResponseEntity<List<BookingWithOwnerResourceDTO>> getUserBookings(@PathVariable Long userId, HttpServletRequest httpRequest);
//
//    @Operation(summary = "Get all bookings",
//            description = "Retrieves all bookings in the system",
//            security = @SecurityRequirement(name = "sessionAuth"))
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Bookings found",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = BookingWithOwnerResourceDTO.class))),
//            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
//            @ApiResponse(responseCode = "403", description = "You do not have the necessary permissions to access this resource.",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ForbiddenResponseSchema.class)))
//    })
//    ResponseEntity<List<BookingWithOwnerResourceDTO>> getAllBookings(HttpServletRequest httpRequest);
//
//    @Operation(summary = "Get bookings by date",
//            description = "Retrieves bookings by a specific date",
//            security = @SecurityRequirement(name = "sessionAuth"))
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Bookings found",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = BookingWithOwnerResourceDTO.class))),
//            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
//            @ApiResponse(responseCode = "404", description = "No bookings found for the specified date",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ResourceNotFoundResponseSchema.class)))
//    })
//    ResponseEntity<List<BookingWithOwnerResourceDTO>> getBookingsByDate(@PathVariable Long date, HttpServletRequest httpRequest);
//
//    @Operation(summary = "Get bookings by user ID",
//            description = "Retrieves bookings for a specific user ID",
//            security = @SecurityRequirement(name = "sessionAuth"))
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Bookings found",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = BookingWithOwnerResourceDTO.class))),
//            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
//            @ApiResponse(responseCode = "403", description = "You do not have the necessary permissions to access this resource.",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ForbiddenResponseSchema.class))),
//            @ApiResponse(responseCode = "404", description = "No bookings found for the specified user ID",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ResourceNotFoundResponseSchema.class)))
//    })
//    ResponseEntity<List<BookingWithOwnerResourceDTO>> getBookingsByUserId(@PathVariable Long userId, HttpServletRequest httpRequest);
//
//    @Operation(summary = "Get bookings by resource ID",
//            description = "Retrieves bookings for a specific resource ID",
//            security = @SecurityRequirement(name = "sessionAuth"))
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Bookings found",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = BookingWithOwnerResourceDTO.class))),
//            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
//            @ApiResponse(responseCode = "404", description = "No bookings found for the specified resource ID",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ResourceNotFoundResponseSchema.class)))
//    })
//    ResponseEntity<List<BookingWithOwnerResourceDTO>> getBookingsByResourceId(@PathVariable Long resourceId, HttpServletRequest httpRequest);
//
//    @Operation(summary = "Get available slots",
//            description = "Retrieves available slots for a specific resource and date",
//            security = @SecurityRequirement(name = "sessionAuth"))
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Available slots found",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = AvailableSlotDTO.class))),
//            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
//            @ApiResponse(responseCode = "400", description = "Invalid request data",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = BookingIllegalArgumentResponseSchema.class)))
//    })
//    ResponseEntity<List<AvailableSlotDTO>> getAvailableSlots(@RequestBody AvailableSlotsRequestDTO request, HttpServletRequest httpRequest);
//
//    @Operation(summary = "Update a booking",
//            description = "Updates an existing booking based on the provided request",
//            security = @SecurityRequirement(name = "sessionAuth"))
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Booking updated successfully",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = BookingDTO.class))),
//            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
//            @ApiResponse(responseCode = "403", description = "You do not have the necessary permissions to access this resource.",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ForbiddenResponseSchema.class))),
//            @ApiResponse(responseCode = "404", description = "Booking not found",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ResourceNotFoundResponseSchema.class)))
//    })
//    ResponseEntity<BookingDTO> updateBooking(@PathVariable Long bookingId, @RequestBody UpdateBookingRequestDTO request, HttpServletRequest httpRequest);
//
//    @Operation(summary = "Delete a booking",
//            description = "Deletes a booking by its ID",
//            security = @SecurityRequirement(name = "sessionAuth"))
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "204", description = "Booking deleted successfully"),
//            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
//            @ApiResponse(responseCode = "403", description = "You do not have the necessary permissions to access this resource.",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ForbiddenResponseSchema.class))),
//            @ApiResponse(responseCode = "404", description = "Booking not found",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ResourceNotFoundResponseSchema.class)))
//    })
//    ResponseEntity<Void> deleteBooking(@PathVariable Long bookingId, HttpServletRequest httpRequest);
//}
