package com.fx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Currency Exchange System.
 *
 * This application grows with you over the next three weeks:
 *   Week 1 — you run it, put it under version control, give it a database,
 *            and build its domain model in plain Java.
 *   Week 2 — you test it, replace the stub API with a real one backed by
 *            MySQL, wire up CI, and put it in a container.
 *   Week 3 — your team ships it as the Full-Stack Financial Application.
 */
@SpringBootApplication
public class FxApplication {

    public static void main(String[] args) {
        SpringApplication.run(FxApplication.class, args);
    }
}
