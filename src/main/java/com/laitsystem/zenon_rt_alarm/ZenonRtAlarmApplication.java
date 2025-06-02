package com.laitsystem.zenon_rt_alarm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ZenonRtAlarmApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZenonRtAlarmApplication.class, args);
	}

}
