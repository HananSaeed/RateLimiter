package com.example.demo.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "subscriptions")
public class subscriptions {
    @Column(name = "subscription_type")
    private String subscription_type;
    @Column(name = "rate_limit_count")
    private int rate_limit_count;
    @Column(name = "time_window")
    private int time_window;

}
