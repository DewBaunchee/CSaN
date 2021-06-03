package by.vorivoda.matvey.model.manager;

import by.vorivoda.matvey.model.dao.entity.StorageInfo;
import org.springframework.stereotype.Component;

@Component
public class FileStorageFactory {
    public FileStorage getFileStorage(StorageInfo storageInfo) {
        return new FileStorage(storageInfo.getId() + "");
    }
}
