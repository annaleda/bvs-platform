package com.example.backendservice1.entities;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.*;

@Entity
@Table(name = "puuser")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iduser;

    @Column(name = "cname")
    private String cname;

    @Column(name = "cpwd")
    private String cpwd;

    // Non serve aggiungere manualmente i getter e setter se stai usando @Data
}
