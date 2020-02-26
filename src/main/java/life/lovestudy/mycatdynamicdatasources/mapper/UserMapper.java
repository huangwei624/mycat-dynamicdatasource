package life.lovestudy.mycatdynamicdatasources.mapper;

import life.lovestudy.mycatdynamicdatasources.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//@Mapper
public interface UserMapper {
	@Select("SELECT * FROM  user_info ")
	public List<UserEntity> findUser();

	@Select("insert into user_info values (#{userName}); ")
	public List<UserEntity> insertUser(@Param("userName") String userName);
}
