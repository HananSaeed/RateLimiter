package com.example.demo.Redis;

import com.example.demo.Repository.SubscriptionsRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.entites.Subscriptions;
import com.example.demo.entites.User;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@AllArgsConstructor
public class RedisController {

    final RedisService redisService;
    final UserRepository userRepository;
    final SubscriptionsRepository subscriptionsRepository;

    @GetMapping("/helloworld")
    public ResponseEntity<HelloWorldResponse> sayHello() {
        return ResponseEntity.ok().body(new HelloWorldResponse("Hello World message"));
    }

    @GetMapping("/long-time")
    @SneakyThrows
    public ResponseEntity<HelloWorldResponse> waitTask() {
        Thread.sleep(80000);
        return ResponseEntity.ok().body(new HelloWorldResponse("Hello World message"));
    }

    @GetMapping("/long-time-cache/{name}")
    @SneakyThrows
    public ResponseEntity<HelloWorldResponse> waitTaskCache(@PathVariable String name) {
        String redisResult = (String) redisService.getData(name);
        String responseMessage = "Welcome: %s, Your exam degree: 90";
        redisService.incKey(name + "_trials");
        if (redisResult == null) { // cache miss
            Thread.sleep(6000);
            responseMessage = String.format(responseMessage, name);
            redisService.saveData(name, responseMessage +"%");
        } else {
            // cache hit
            responseMessage = redisResult;
        }
        return ResponseEntity.ok().body(new HelloWorldResponse(responseMessage));
    }

    @GetMapping("/long-time-cache-auto/{name}")
    @SneakyThrows
    @Cacheable(value = "responseMessage", key = "#name")
    public HelloWorldResponse waitTaskCacheAuto(@PathVariable String name) {
        String responseMessage = String.format("Welcome: %s, Your exam degree: 90%%",name);
        Thread.sleep(6000);
        System.out.println("Call wait task with parameters: " + name);
        return new HelloWorldResponse(responseMessage);
    }

    @GetMapping("/limited-endpoint/{userId}")
    public String rateLimitedEndpoint(@PathVariable int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        int subscription_id = user.getSubscription_id();
        int rateLimit = subscriptionsRepository.findById(subscription_id).orElseThrow(() -> new RuntimeException("Subscription not found")).getRate_limit_count();
        int time_window = subscriptionsRepository.findById(subscription_id).orElseThrow(() -> new RuntimeException("Subscription not found")).getTime_window();
        String key = "rate_limit:" + userId + ":" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm"));
        Object currentCountObj = redisService.getData(key);
        int currentCount = currentCountObj == null ? 0 : (int) currentCountObj;
        if (currentCount >= rateLimit) {
            return "Rate limit exceeded. Try again later.";
        } else {
            redisService.incKey(key);
            redisService.saveDataWithExpiration(key, currentCount + 1,time_window);
            return "Request processed.";
        }
    }

}
