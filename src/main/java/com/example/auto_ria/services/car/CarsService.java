package com.example.auto_ria.services.car;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.auto_ria.dao.cars.CarDaoSQL;
import com.example.auto_ria.dao.filters.CarSpecification;
import com.example.auto_ria.dto.CarDTO;
import com.example.auto_ria.dto.updateDTO.CarUpdateDTO;
import com.example.auto_ria.enums.EBrand;
import com.example.auto_ria.enums.ECurrency;
import com.example.auto_ria.enums.EModel;
import com.example.auto_ria.enums.ERole;
import com.example.auto_ria.exceptions.car.CarAlreadyActivatedException;
import com.example.auto_ria.exceptions.car.CarIsBannedException;
import com.example.auto_ria.exceptions.car.CarNotFoundException;
import com.example.auto_ria.exceptions.file.FileTransferException;
import com.example.auto_ria.exceptions.general.DatabaseOperationException;
import com.example.auto_ria.exceptions.verification.ProfanityFoundException;
import com.example.auto_ria.kafka.ViewEventProducer;
import com.example.auto_ria.mail.MailerService;
import com.example.auto_ria.models.car.CarSQL;
import com.example.auto_ria.models.responses.car.CarQuery;
import com.example.auto_ria.models.responses.car.CarResponse;
import com.example.auto_ria.models.responses.car.MiddlePriceResponse;
import com.example.auto_ria.models.responses.statistics.StatisticsResponse;
import com.example.auto_ria.models.responses.user.UserCarResponse;
import com.example.auto_ria.models.user.UserSQL;
import com.example.auto_ria.services.auth.PermissionService;
import com.example.auto_ria.services.currency.CurrencyRateService;
import com.example.auto_ria.services.otherApi.CitiesService;
import com.example.auto_ria.services.otherApi.ProfanityFilterService;
import com.example.auto_ria.services.user.UsersServiceSQL;
import com.example.auto_ria.services.validation.car.CarValidationService;
import com.example.auto_ria.services.validation.car.CarViewCacheService;
import com.example.auto_ria.services.validation.files.FileService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class CarsService {

    private static final int PAGE_SIZE = 2;

    private CarDaoSQL carDAO;
    private MailerService mailer;
    private PermissionService permissionService;
    private UsersServiceSQL usersService;
    private ProfanityFilterService profanityFilterService;
    private CitiesService citiesService;
    private FileService fileService;
    private CarValidationService carValidationService;
    private ViewEventProducer viewEventProducer;
    private HttpServletRequest request;
    private CarViewCacheService carViewCacheService;
    private CurrencyRateService currencyRateService;

    private static final AtomicInteger validationFailureCounter = new AtomicInteger(0);

    public MiddlePriceResponse getMiddlePrice(int id, ECurrency currency) {
        permissionService.validatePremiumPermission();
        CarSQL carSQL = extractById(id);
        isActivatedCheck(carSQL);
        int middlePrice = carDAO.findAveragePriceByBrandModelAndCity(carSQL.getBrand().toString(),
                carSQL.getModel().toString(),
                carSQL.getUser().getCity());

        if (currency.equals(ECurrency.EUR)) {
            return MiddlePriceResponse.builder()
                    .currency(currency)
                    .middlePrice(middlePrice)
                    .build();
        }

        int convertedPrice = currencyRateService.convertFromEUR(middlePrice, currency);

        return MiddlePriceResponse.builder()
                .currency(currency)
                .middlePrice(convertedPrice)
                .build();
    }

    public Page<CarResponse> getAll(String brand, String model, boolean isActivated, int page, String currency) {
        carValidationService.isValidCurrency(currency);
        CarQuery query = CarQuery.builder()
                .brand(brand != null ? carValidationService.convertStringToEBrand(brand) : null)
                .model(model != null ? carValidationService.convertStringToEModel(model) : null)
                .isActivated(isActivated)
                .page(page)
                .build();

        if (!query.isActivated())
            permissionService.isAuthority();

        page = Math.max(query.getPage() - 1, 0);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);

        Specification<CarSQL> spec = CarSpecification.filterBy(query);
        Page<CarSQL> carsPage = carDAO.findAll(spec, pageable);

        return carsPage.map(car -> formCarResponse(car, ECurrency.valueOf(currency)));
    }

    private void isActivatedCheck(CarSQL car) {
        if (!car.isActivated()) {
            throw new IllegalArgumentException("The car is temporally banned");
        }
    }

    private String getClientIpAddress() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String getUserAgent() {
        return request.getHeader("User-Agent");
    }

    public CarResponse getById(int id, ECurrency currency) {
        CarSQL carSQL = extractById(id);
        if (!carSQL.isActivated())
            permissionService.isAuthority();
        viewEventProducer.sendViewEvent(
                Long.valueOf(carSQL.getId()),
                Long.valueOf(carSQL.getUser().getId()),
                getClientIpAddress(),
                getUserAgent());
        return formCarResponse(carSQL, currency);
    }

    public CarSQL extractById(int id) {
        return carDAO.findById(id)
                .orElseThrow(() -> new CarNotFoundException("Car doesn't exist"));
    }

    private void isNotActivated(CarSQL carSQL) {
        if (carSQL.isActivated()) {
            throw new CarAlreadyActivatedException("The car is already active");
        }
    }

    private void isAlreadyBanned(CarSQL carSQL) {
        if (!carSQL.isActivated()) {
            throw new CarIsBannedException("The car is already banned");
        }
    }

    public String activate(int id) {
        CarSQL carSQL = extractById(id);
        isNotActivated(carSQL);
        carSQL.setActivated(true);
        carDAO.save(carSQL);
        mailer.handleEmail(
                () -> mailer.sendCarActivatedEmail(carSQL.getUser().getEmail(), carSQL.getUser().getFullName(), id));
        return "Car activated successfully";
    }

    public String ban(int id) {
        CarSQL carSQL = extractById(id);
        isAlreadyBanned(carSQL);
        carSQL.setActivated(false);
        carDAO.save(carSQL);
        mailer.handleEmail(
                () -> mailer.sendCarBannedEmail(carSQL.getUser().getEmail(), carSQL.getUser().getFullName()));
        return "Car banned successfully";
    }

    public Page<CarResponse> getByUser(UserSQL user, int page, ECurrency currency) {
        try {
            Pageable pageable = PageRequest.of(page, PAGE_SIZE);
            Page<CarSQL> carsPage = carDAO.findAllByUser(user, pageable);
            Page<CarResponse> carResponsesPage = carsPage.map(car -> formCarResponse(car, currency));
            return carResponsesPage;
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to save car: " + e.getMessage());
        }
    }

    public StatisticsResponse getStatistics(int id) {
        extractById(id);
        return carViewCacheService.getViewsCount(Long.valueOf(id));
    }

    public Page<CarResponse> getByUserActivatedOnly(int id, int page, String currency) {
        carValidationService.isValidCurrency(currency);
        UserSQL user = usersService.getById(id);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<CarSQL> carsPage = carDAO.findAllByUserAndActivatedTrue(user, pageable);
        Page<CarResponse> carResponsesPage = carsPage.map(car -> formCarResponse(car, ECurrency.valueOf(currency)));
        return carResponsesPage;
    }

    public void save(CarSQL car) {
        try {
            carDAO.save(car);
        } catch (Exception e) {
            log.error("Failed to save car: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to save car: " + e.getMessage());
        }
    }

    public List<CarSQL> findAllByUser(UserSQL user) {
        try {
            return carDAO.findByUser(user);
        } catch (Exception e) {
            log.error("Failed to fetch cars by user: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to save car: " + e.getMessage());
        }
    }

    public void deleteAllByUser(UserSQL user) {
        try {
            carDAO.deleteAllByUser(user);
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to save car: " + e.getMessage());
        }
    }

    private void informDeactivateCar(String descr, String filtered) {
        List<UserSQL> managers = usersService.getListByRole(ERole.MANAGER);

        UserSQL user = permissionService.getAuthenticatedUser();

        managers.forEach(managerSQLItem -> {
            mailer.handleEmail(() -> mailer.sendCheckAnnouncementEmail(user.getEmail(), descr));
        });
        mailer.handleEmail(() -> mailer.sendCarBeingCheckedEmail(user.getEmail(), user.getFullName(), filtered));
    }

    private int calulateBasePriceEUR(ECurrency currentCurrency, int currentPrice) {
        return currentCurrency.equals(ECurrency.EUR) ? currentPrice
                : currencyRateService.convertToEUR(currentPrice, currentCurrency);
    }

    private CarSQL formCarObjAndSave(CarDTO carDTO, UserSQL user) {

        CarSQL car = CarSQL.builder()
                .priceBase(calulateBasePriceEUR(ECurrency.valueOf(carDTO.getCurrency()), carDTO.getPrice()))
                .brand(EBrand.valueOf(carDTO.getBrand()))
                .model(EModel.valueOf(carDTO.getModel()))
                .powerH(carDTO.getPowerH())
                .city(carDTO.getCity())
                .country(carDTO.getCountry())
                .user(user)
                .isActivated(carDTO.isActivated())
                .price(carDTO.getPrice())
                .currency(ECurrency.valueOf(carDTO.getCurrency()))
                .description(carDTO.getDescription())
                .build();

        List<String> fileNames = fileService.uploadFiles(carDTO.getPhotos());
        car.setPhoto(fileNames);

        return carDAO.save(car);
    }

    private String checkProfanity(String desc) {
        String filteredText = profanityFilterService.containsProfanity(desc);

        if (profanityFilterService.containsProfanityBoolean(filteredText, desc)) {
            int currentCount = validationFailureCounter.incrementAndGet();
            if (currentCount < 4) {
                int attemptsLeft = 4 - currentCount;
                throw new ProfanityFoundException("Consider editing your description. " +
                        "Profanity found - attempts left:  " + attemptsLeft);
            }
        }

        return filteredText;
    }

    public CarResponse post(CarDTO carDTO) {
        carValidationService.validateCarEnums(carDTO);
        citiesService.isCityInCountry(carDTO.getCity(), carDTO.getCountry());
        permissionService.allowedToPostCar();
        String filteredText = checkProfanity(carDTO.getDescription());
        boolean shouldActivate = validationFailureCounter.get() <= 4;
        CarSQL car = saveCar(carDTO, shouldActivate);

        if (!shouldActivate) {
            informDeactivateCar(carDTO.getDescription(), filteredText);
        }

        currencyRateService.setCarPrices(car.getId(), carDTO.getPrice(), car.getCurrency());

        return formCarResponse(car, car.getCurrency());
    }

    private CarSQL saveCar(CarDTO carDTO, boolean isActive) {
        carDTO.setActivated(isActive);
        return formCarObjAndSave(carDTO, permissionService.allowedToPostCar());
    }

    public String deleteById(int id) {
        CarSQL car = extractById(id);
        permissionService.allowedToModifyCar(car);

        fileService.deleteFiles(car.getPhoto());
        carDAO.deleteById(id);
        mailer.handleEmail(() -> mailer.sendCarBannedEmail(car.getUser().getEmail(), car.getUser().getFullName()));
        return "Car was deleted";
    }

    private void checkProfanityOnUpdate(String desc) {
        if (desc != null) {
            String filteredText = checkProfanity(desc);
            if (profanityFilterService.containsProfanityBoolean(filteredText, desc)) {
                throw new ProfanityFoundException(
                        "Profanity found. Consider editing the description: " + filteredText);
            }
        }
    }

    public CarResponse updateCar(int id, CarUpdateDTO carDTO) {
        CarSQL car = extractById(id);
        permissionService.allowedToModifyCar(car);
        if ((carDTO.getCity() != null) || (carDTO.getRegion() != null))
            citiesService.isCityInCountry(carDTO.getCity(), carDTO.getRegion());

        checkProfanityOnUpdate(carDTO.getDescription());
        updateCarFields(car, carDTO);
        int priceAsInt = (int) car.getPrice();

        if (!carDTO.getCurrency().equals(car.getCurrency()) || (carDTO.getPrice() != priceAsInt)) {
            currencyRateService.setCarPrices(car.getId(), carDTO.getPrice(), carDTO.getCurrency());
            car.setPriceBase(calulateBasePriceEUR(car.getCurrency(), priceAsInt));
        }

        CarSQL carSQL = carDAO.save(car);
        return formCarResponse(carSQL, carSQL.getCurrency());
    }

    private CarSQL updateCarFields(CarSQL car, CarUpdateDTO carDTO) {
        try {
            Class<?> carDTOClass = carDTO.getClass();
            Field[] fields = carDTOClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldValue = field.get(carDTO);
                if (fieldValue != null) {
                    Field carField = CarSQL.class.getDeclaredField(field.getName());
                    carField.setAccessible(true);
                    if (field.getName().equals("currency")) {
                        carField.set(car, ECurrency.valueOf(fieldValue.toString()));
                    } else {
                        carField.set(car, fieldValue);
                    }
                }
            }
            return car;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Error while updating");
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Error while updating");
        }
    }

    public List<String> deleteCarPhotos(int id, List<String> deletePictures) {
        CarSQL car = extractById(id);
        permissionService.allowedToModifyCar(car);

        if (deletePictures == null || deletePictures.isEmpty()) {
            throw new FileTransferException("No pictures specified for deletion");
        }

        List<String> allPictures = car.getPhoto();
        allPictures.removeAll(deletePictures);

        fileService.deleteFiles(allPictures);
        car.setPhoto(allPictures);
        save(car);
        return allPictures;
    }

    public List<String> patchCarPhotos(int id, MultipartFile[] newPhotos) {
        CarSQL car = extractById(id);
        permissionService.validatePermissionToModifyUser(car.getUser());

        if (newPhotos == null) {
            throw new ProfanityFoundException("No photos specified for upload");
        }

        List<String> existingPhotoNames = car.getPhoto();
        List<String> newPhotoNames = fileService.uploadFiles(newPhotos);
        existingPhotoNames.addAll(newPhotoNames);

        car.setPhoto(existingPhotoNames);
        save(car);
        return existingPhotoNames;
    }

    private CarResponse formCarResponse(CarSQL carSQL, ECurrency currency) {

        Double priceInCurrency = Double.valueOf(carSQL.getPrice());
        if (!carSQL.getCurrency().equals(currency)) {
            if (!carSQL.getCurrency().equals(currency)) {
                priceInCurrency = (double) currencyRateService.getCarPriceInCurrency(carSQL.getId(), currency);
            }
        }

        double roundedValue = new BigDecimal(priceInCurrency)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        return CarResponse.builder()
                .id(carSQL.getId())
                .brand(carSQL.getBrand())
                .powerH(carSQL.getPowerH())
                .city(carSQL.getCity())
                .country(carSQL.getCountry())
                .model(carSQL.getModel())
                .price(roundedValue)
                .currency(currency)
                .photo(carSQL.getPhoto())
                .isActivated(carSQL.isActivated())
                .user(UserCarResponse.builder()
                        .id(carSQL.getUser().getId())
                        .name(carSQL.getUser().getName())
                        .lastName(carSQL.getUser().getLastName())
                        .avatar(carSQL.getUser().getAvatar())
                        .city(carSQL.getUser().getCity())
                        .country(carSQL.getUser().getCountry())
                        .role(carSQL.getUser().getRoles().get(0))
                        .number(carSQL.getUser().getNumber())
                        .createdAt(carSQL.getUser().getCreatedAt())
                        .build())
                .description(carSQL.getDescription())
                .createdAt(carSQL.getCreatedAt())
                .build();
    }
}