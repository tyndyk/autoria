package com.example.auto_ria.dao.cars;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.auto_ria.models.car.CarView;

@Repository
public interface CarViewRepository
        extends JpaRepository<CarView, Integer>, JpaSpecificationExecutor<CarView>{

}