package by.vorivoda.matvey.model.dao.entity;

import javax.persistence.*;

@Entity
@Table(name = "STORAGES")
public class StorageInfo implements DAO {

    @Id
    @Column(name = "ID")
    @GeneratedValue
    private Long id;

    public StorageInfo() {
        id = 0L;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
