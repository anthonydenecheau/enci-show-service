package com.scc.enci.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.scc.enci.model.Judge;

@Repository
public interface JudgeRepository extends CrudRepository<Judge, String> {
	
    public Judge findById(int id);
    
    public List<Judge> findByCountryAndIsInternationalOrderByLastNameAscFirstNameAsc(String country, String isInternational);
    public List<Judge> findByIsInternationalOrderByLastNameAscFirstNameAsc(String isInternational);
    public List<Judge> findByCountryOrderByLastNameAscFirstNameAsc(String country);
    
}
