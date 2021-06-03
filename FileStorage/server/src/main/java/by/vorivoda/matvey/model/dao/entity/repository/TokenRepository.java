package by.vorivoda.matvey.model.dao.entity.repository;

import by.vorivoda.matvey.security.SecurityToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<SecurityToken, Long> {
    SecurityToken findByValue(String value);
}
