package by.vorivoda.matvey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"by.vorivoda.matvey.model.dao.entity.repository"})
public class FileStorageServer {

    public static void main(String[] args) {
        SpringApplication.run(FileStorageServer.class, args);
    }
}
