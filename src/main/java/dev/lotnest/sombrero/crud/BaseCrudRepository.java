package dev.lotnest.sombrero.crud;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Optional;

public interface BaseCrudRepository<T, ID extends Serializable> {

    <S extends T> @NotNull S save(@NotNull S entity);

    <S extends T> @NotNull Iterable<S> saveAll(@NotNull Iterable<S> entities);

    @NotNull Optional<T> findById(@NotNull ID id);

    boolean existsById(@NotNull ID id);

    @NotNull Iterable<T> findAll();

    @NotNull Iterable<T> findAllById(@NotNull Iterable<ID> ids);

    long count();

    void deleteById(@NotNull ID id);

    void delete(@NotNull T entity);

    void deleteAllById(@NotNull Iterable<? extends ID> ids);

    void deleteAll(@NotNull Iterable<? extends T> entities);

    void deleteAll();
}
