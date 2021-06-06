package ru.vsu.ofoodApi.oFoodApi.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.ofoodApi.oFoodApi.entities.Employee;

@Repository
public interface EmployeeRepo extends ReactiveMongoRepository<Employee, String> {
}