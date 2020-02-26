package life.lovestudy.mycatdynamicdatasources.service;

import life.lovestudy.mycatdynamicdatasources.entity.UserEntity;
import life.lovestudy.mycatdynamicdatasources.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
	@Autowired
	private UserMapper userMapper;

	public List<UserEntity> findUser() {
		return userMapper.findUser();
	}

	public List<UserEntity> insertUser(String userName) {
		return userMapper.insertUser(userName);
	}

}
