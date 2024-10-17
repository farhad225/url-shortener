package org.dkb.hash.generation.service.config;

import io.seruco.encoding.base62.Base62;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AppConfig {

	@Bean
	public Base62 base62Encoder() {
		return Base62.createInstance();
	}

}
