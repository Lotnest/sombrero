package dev.lotnest.sombrero.crud;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseCrudRepositoryImpl<T, ID extends Serializable> extends BaseCrudRepository<T, ID>, CrudRepository<T, ID> {
}
