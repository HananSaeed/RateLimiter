package com.example.demo.Redis;

import com.example.demo.Repository.SubscriptionsRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.entites.User;
import com.example.demo.Log.LoggerConfig;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@RestController
@AllArgsConstructor
public class RedisController {

    final RedisService redisService;
    final UserRepository userRepository;
    final SubscriptionsRepository subscriptionsRepository;
    private static final Logger logger = LoggerConfig.getLogger();

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
    @PostMapping("/users")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        return ResponseEntity.ok().body(userRepository.save(user));
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
    public ResponseEntity<String> rateLimitedEndpoint(@PathVariable int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        int subscription_id = user.getSubscription_id();
        int rateLimit = subscriptionsRepository.findById(subscription_id).orElseThrow(() -> new RuntimeException("Subscription not found")).getRate_limit_count();
        int time_window = subscriptionsRepository.findById(subscription_id).orElseThrow(() -> new RuntimeException("Subscription not found")).getTime_window();
        String subscriptionType = subscriptionsRepository.findById(subscription_id).orElseThrow(() -> new RuntimeException("Subscription not found")).getSubscription_type();
        String key = "rate_limit:count:" + userId;
        System.out.println("Key: " + key);
        Object currentCountObj = redisService.getData(key);
        int currentCount = currentCountObj == null ? 0 : (int) currentCountObj;

        if (currentCount >= rateLimit) {
            logger.warning("User " + userId + " with subscription " + subscriptionType + " exceeded the rate limit at " + LocalDateTime.now());
             return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded. Try again later.");
        } else {
            redisService.incKey(key);
            redisService.saveDataWithExpiration(key, currentCount + 1, time_window);
            return ResponseEntity.ok().body("Request processed.");
        }
    }

    @GetMapping("/sliding-window-limited-endpoint/{userId}")
    public ResponseEntity<String> slidingWindowRateLimitedEndpoint(@PathVariable int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        int subscriptionId = user.getSubscription_id();
        int rateLimit = subscriptionsRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"))
                .getRate_limit_count();
        int timeWindow = subscriptionsRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"))
                .getTime_window();
        String subscriptionType = subscriptionsRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"))
                .getSubscription_type();
        String key = "rate_limit:timestamps:" + userId;

        long currentTime = Instant.now().getEpochSecond();
        redisService.removeOldTimestamps(key, currentTime - timeWindow);

        Set<Object> timestamps = redisService.getTimestamps(key, currentTime - timeWindow, currentTime);
        int currentCount = timestamps.size();

        if (currentCount >= rateLimit) {
            logger.warning("User " + userId + " with subscription " + subscriptionType + " exceeded the rate limit at " + Instant.now());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded. Try again later.");
        } else {
            redisService.saveTimestamp(key, currentTime);
            redisService.setExpiration(key, timeWindow , TimeUnit.SECONDS);
            return ResponseEntity.ok().body("Request processed.");
        }
    }


}