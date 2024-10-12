package com.example.demo.Repository;

import com.example.demo.entites.Subscriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionsRepository extends JpaRepository<Subscriptions, Integer> {
}
