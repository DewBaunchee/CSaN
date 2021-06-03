package by.vorivoda.matvey.model.dao.entity.repository;

import by.vorivoda.matvey.model.dao.entity.StorageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageInfoRepository extends JpaRepository<StorageInfo, Long> {
}