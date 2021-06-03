package by.vorivoda.matvey.model.dao.entity.repository;

import by.vorivoda.matvey.model.dao.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
}