/*
package by.vorivoda.matvey.services;

import by.vorivoda.matvey.entities.UserInfo;
import by.vorivoda.matvey.entities.repositories.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoService implements DAOService<UserInfo> {

    private final UserInfoRepository userInfoRepository;

    @Autowired
    public UserInfoService(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public boolean create(UserInfo userInfo) {
        if (userInfoRepository.findById(userInfo.getId()).isPresent()) return false;

        userInfoRepository.save(userInfo);
        return true;
    }

    @Override
    public UserInfo get(Integer id) {
        return userInfoRepository.getById(id);
    }

    @Override
    public List<UserInfo> getAll() {
        return userInfoRepository.findAll();
    }

    @Override
    public boolean update(Integer id, UserInfo userInfo) {
        if (delete(id)) {
            create(userInfo);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        if (userInfoRepository.existsById(id)) {
            userInfoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
*/
