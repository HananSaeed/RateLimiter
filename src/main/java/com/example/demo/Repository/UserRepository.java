package com.example.demo.Repository;

import com.example.demo.entites.users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<users, Integer> {
}
