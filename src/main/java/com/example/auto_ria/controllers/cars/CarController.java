package com.example.auto_ria.controllers.cars;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.auto_ria.dto.CarDTO;
import com.example.auto_ria.dto.responces.PaginatedResponse;
import com.example.auto_ria.dto.responces.ResponceObj;
import com.example.auto_ria.dto.responces.builder.ResponseBuilder;
import com.example.auto_ria.dto.updateDTO.CarUpdateDTO;
import com.example.auto_ria.enums.EBrand;
import com.example.auto_ria.enums.ECurrency;
import com.example.auto_ria.enums.EModel;
import com.example.auto_ria.models.responses.car.CarResponse;
import com.example.auto_ria.models.responses.car.MiddlePriceResponse;
import com.example.auto_ria.models.responses.statistics.StatisticsResponse;
import com.example.auto_ria.services.car.CarsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/v1/cars")
@Tag(name = "Car API", description = "Operations related to cars")
public class CarController {

    private CarsService carsService;

    @Operation(summary = "Get all cars and queries them if needed", description = "Returns a list of all cars with pagination", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved cars"),
            @ApiResponse(responseCode = "401", description = "Special access needed to fetch banned cars"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("")
    public ResponseEntity<ResponceObj<PaginatedResponse<CarResponse>>> getAllPageQuery(
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "model", required = false) String model,
            @RequestParam(value = "powerH", required = false) Integer powerH,
            @RequestParam(value = "isActivated", defaultValue = "true") boolean isActivated,
            @RequestParam(value = "currency", defaultValue = "EUR") String currency,
            @RequestParam(value = "page", defaultValue = "1") int page) {
        return ResponseBuilder.buildPagedResponse(carsService.getAll(brand, model, isActivated, page, currency));
    }

    @Operation(summary = "Activate a car if it was banned", description = "Returns a success message if activating worked", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully activated car"),
            @ApiResponse(responseCode = "401", description = "Car is already activated or activation key is missing or malformed"),
            @ApiResponse(responseCode = "404", description = "Car not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping("/activate/{id}")
    public ResponseEntity<ResponceObj<ResponseEntity<String>>> activate(
            @PathVariable("id") int id) {
        return ResponseBuilder.buildResponse(carsService.activate(id));
    }

    @Operation(summary = "Ban a car if the description, pictures etc. violated the policy", description = "Returns a success message when banned worked", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully banned car"),
            @ApiResponse(responseCode = "401", description = "Car is already banned or unauthorized to ban cars"),
            @ApiResponse(responseCode = "404", description = "Car not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping("/ban/{id}")
    public ResponseEntity<ResponceObj<String>> banCar(
            @PathVariable("id") int id) {
        return ResponseBuilder.buildResponse(carsService.ban(id));
    }

    @Operation(summary = "Gets and compares the prices for this auto model in the region. Premium needed", description = "Returns middle prices in currencies", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved middle price"),
            @ApiResponse(responseCode = "401", description = "Following reasons: 1. Car is banned. 2. No premium"),
            @ApiResponse(responseCode = "404", description = "Car not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @GetMapping("/middle/{id}")
    public ResponseEntity<ResponceObj<MiddlePriceResponse>> middle(
            @PathVariable("id") int id,
            @RequestParam("currency") ECurrency currency) {
        return ResponseBuilder.buildResponse(carsService.getMiddlePrice(id, currency));
    }

    @Operation(summary = "Get cars of a specific seller", description = "Returns a list of cars of a specific seller", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved cars"),
            @ApiResponse(responseCode = "401", description = "Currency is invalid"),
            @ApiResponse(responseCode = "404", description = "Seller not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/by-user")
    public ResponseEntity<ResponceObj<PaginatedResponse<CarResponse>>> getAllBySeller(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "currency") String currency,
            @RequestParam(value = "id") int id) {
        return ResponseBuilder.buildPagedResponse(carsService.getByUserActivatedOnly(id, page, currency));
    }

    @Operation(summary = "Get how many times a specific car was viewed. Premium needed", description = "Returns the number of times a car was viewed", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics"),
            @ApiResponse(responseCode = "404", description = "Car not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/statistics/{id}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ResponceObj<StatisticsResponse>> getStatistics(
            @PathVariable("id") int id) {
        return ResponseBuilder.buildResponse(carsService.getStatistics(id));
    }

    @Operation(summary = "Get car by ID.", description = "Returns a car by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved car"),
            @ApiResponse(responseCode = "404", description = "Car not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponceObj<CarResponse>> getById(
            @PathVariable("id") int id,
            @RequestParam("currency") ECurrency currency) {
        return ResponseBuilder.buildResponse(carsService.getById(id, currency));
    }

    @Operation(summary = "Create a car to sell. Premium needed if more than one car", description = "Returns the created car details", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully posted car"),
            @ApiResponse(responseCode = "401", description = "Premium is required if more than one car is posted or access token is invalid"),
            @ApiResponse(responseCode = "400", description = "Reasons: 1. Invalid car fields were found. 2. Profanity found. 3. Pictures are not valid"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('USER')")
    @PostMapping("")
    public ResponseEntity<ResponceObj<CarResponse>> post(
            @ModelAttribute @Valid CarDTO carDTO) {
        return ResponseBuilder.buildResponse(carsService.post(carDTO));
    }

    @Operation(summary = "Update a car", description = "Returns an updated version of the car", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully patched car"),
            @ApiResponse(responseCode = "404", description = "Car not found"),
            @ApiResponse(responseCode = "401", description = "Not allowed to modify car"),
            @ApiResponse(responseCode = "400", description = "Reasons: 1. Invalid car fields were found. 2. Profanity found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{id}")
    public ResponseEntity<ResponceObj<CarResponse>> patchCar(@PathVariable int id,
            @RequestBody @Valid CarUpdateDTO partialCar) {
        return ResponseBuilder.buildResponse(carsService.updateCar(id, partialCar));
    }

    @Operation(summary = "Delete specific pictures from a car post. Invalid file names are ignored", description = "Returns a success message", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted car photos"),
            @ApiResponse(responseCode = "404", description = "Car not found"),
            @ApiResponse(responseCode = "401", description = "Not allowed to modify car"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("delete-pictures/{id}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ResponceObj<List<String>>> deletePhotos(@PathVariable int id,
            @RequestBody List<String> photoNames) {
        return ResponseBuilder.buildResponse(carsService.deleteCarPhotos(id, photoNames));
    }

    @Operation(summary = "Add pictures to the post", description = "Returns a success message", responses = {
        @ApiResponse(responseCode = "200", description = "Successfully added car photos"),
        @ApiResponse(responseCode = "404", description = "Car not found"),
        @ApiResponse(responseCode = "400", description = "Files are invalid"),
        @ApiResponse(responseCode = "401", description = "Not allowed to modify car"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
})
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @PostMapping("/add-pictures/{id}")
    public ResponseEntity<ResponceObj<List<String>>> patchPhotos(@PathVariable int id,
            @RequestParam("photos") MultipartFile[] newPictures) {
        return ResponseBuilder.buildResponse(carsService.patchCarPhotos(id, newPictures));
    }

    @Operation(summary = "Delete a car post by id", description = "Returns a success message", responses = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted car"),
        @ApiResponse(responseCode = "404", description = "Car not found"),
        @ApiResponse(responseCode = "401", description = "Not allowed to delete car"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
})
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponceObj<String>> deleteById(@PathVariable int id) {
        return ResponseBuilder.buildResponse(carsService.deleteById(id));
    }

    @Operation(summary = "Get available brands applicable to car posts", description = "Returns a list of brands applicable to car posts")
    @GetMapping("/brands")
    public ResponseEntity<ResponceObj<EBrand[]>> getBrands() {
        return ResponseBuilder.buildResponse(EBrand.values());
    }

    @Operation(summary = "Get available models of brand applicable to car posts", description = "Returns a list of models by brand applicable to car posts")
    @GetMapping("/brands/{brand}/models")
    public ResponseEntity<ResponceObj<EModel[]>> getBrandsModels(@PathVariable("brand") String brand) {
        return ResponseBuilder.buildResponse(Arrays.stream(EModel.values())
                .filter(eModel -> eModel.getBrand().name().matches(brand))
                .toArray(EModel[]::new));
    }
}
