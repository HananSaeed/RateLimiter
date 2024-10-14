package com.example.demo.entites;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "User")
@Entity
public class User {
    // make it auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_id;
    @Column(name = "username")
    private String username;
    @Column(name = "subscription_id")
    private int subscription_id;

}


