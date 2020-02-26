package life.lovestudy.mycatdynamicdatasources;

import life.lovestudy.mycatdynamicdatasources.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

@Slf4j
@SpringBootApplication
@MapperScan("life.lovestudy.mycatdynamicdatasources.mapper")
public class App {
	
	@Autowired
	private UserMapper userMapper;
	
	public static void main(String[] args){
		SpringApplication.run(App.class, args);
	}
	
	@PostConstruct
	public void init(){
		log.info("---------->>"+userMapper);
	}
	
}
