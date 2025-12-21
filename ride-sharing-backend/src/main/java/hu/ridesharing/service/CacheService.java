package hu.ridesharing.service;

import org.springframework.data.jpa.repository.JpaRepository;

public class CacheService {

    public static <T> boolean  isCacheable(JpaRepository<T, ?> repository) {
        return true;
    }
}
