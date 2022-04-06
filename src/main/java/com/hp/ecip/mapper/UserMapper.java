package com.hp.ecip.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.hp.ecip.bo.User;

@Mapper
public interface UserMapper {
	
	@Select("select user_id, instance_id, party_id, user_name, user_password, home_directory, "
			+ "enable_flag, write_permission, max_login_number, max_login_perip,"
			+ "idle_time, upload_rate, download_rate "
			+ "from F_T_User where enable_flag=1 "
			+ "and instance_id = #{instanceId}")
	public List<User> getUserListByInstanceId(String instanceId);
	
	
	
	@Select(" select u.USER_NAME, u.USER_PASSWORD,  u.PARTY_ID, u.INSTANCE_ID, p.PARTY_NAME "
			+ "from f_t_user u, f_t_party p "
			+ "where u.INSTANCE_ID = p.INSTANCE_ID and u.PARTY_ID = p.PARTY_ID "
			+ "and USER_ID = #{userId}")
	public User getUserByUserId(String userId);
}
