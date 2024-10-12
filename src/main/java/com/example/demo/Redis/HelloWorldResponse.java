package com.example.demo.Redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@ToString
public class HelloWorldResponse implements Serializable {
    String message;
}
