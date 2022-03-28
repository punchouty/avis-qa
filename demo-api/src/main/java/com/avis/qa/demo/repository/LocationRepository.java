package com.avis.qa.demo.repository;

import com.avis.qa.demo.domain.Location;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface LocationRepository extends PagingAndSortingRepository<Location, Long> {

}
