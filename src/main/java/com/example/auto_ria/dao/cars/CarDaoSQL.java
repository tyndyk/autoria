package com.example.auto_ria.dao.cars;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.auto_ria.models.car.CarSQL;
import com.example.auto_ria.models.user.UserSQL;

@Repository
public interface CarDaoSQL
                extends JpaRepository<CarSQL, Integer>, JpaSpecificationExecutor<CarSQL> {

        List<CarSQL> findByUser(UserSQL userSQL);

        void deleteAllByUser(UserSQL userSQL);

        Page<CarSQL> findAllByUser(UserSQL userSQL, Pageable pageable);

        @Query("SELECT p FROM CarSQL p WHERE p.user = :user AND p.isActivated = true")
        Page<CarSQL> findAllByUserAndActivatedTrue(@Param("user") UserSQL userSQL, Pageable pageable);

        @Query("SELECT AVG(c.priceBase) FROM CarSQL c WHERE c.brand = :brand AND c.model = :model AND c.city = :city")
        int findAveragePriceByBrandModelAndCity(@Param("brand") String brand,
                        @Param("model") String model,
                        @Param("city") String city);

        long count();
}