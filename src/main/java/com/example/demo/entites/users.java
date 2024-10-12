package com.example.demo.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "users")
@Entity
public class users {
    @Id
    private int user_id;
    @Column(name = "username")
    private String username;
    @Column(name = "subscription_type")
    private String subscription_type;

}


