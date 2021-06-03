/*
package by.vorivoda.matvey.services;

import by.vorivoda.matvey.entities.StorageInfo;
import by.vorivoda.matvey.entities.repositories.StorageInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StorageService implements DAOService<StorageInfo>{

    private final StorageInfoRepository storageInfoRepository;

    @Autowired
    public StorageService(StorageInfoRepository storageInfoRepository) {
        this.storageInfoRepository = storageInfoRepository;
    }

    @Override
    public boolean create(StorageInfo storageInfo) {
        if(storageInfoRepository.findById(storageInfo.getId()).isPresent()) return false;

        storageInfoRepository.save(storageInfo);
        return true;
    }

    @Override
    public StorageInfo get(Integer id) {
        return storageInfoRepository.getById(id);
    }

    @Override
    public List<StorageInfo> getAll() {
        return storageInfoRepository.findAll();
    }

    @Override
    public boolean update(Integer id, StorageInfo storageInfo) {
        if (delete(id)) {
            create(storageInfo);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        if (storageInfoRepository.existsById(id)) {
            storageInfoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
*/
