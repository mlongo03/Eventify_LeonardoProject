package com.eventify.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.eventify.app.service.EventReminderScheduler;

@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(AppApplication.class, args);

		EventReminderScheduler eventReminderScheduler = context.getBean(EventReminderScheduler.class);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			eventReminderScheduler.shutdown();
		}));
	}

}
