package com.hp.ecip.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.hp.ecip.bo.Route;

@Mapper
public interface RouteMapper {
	
	@Select("select route_id, business_id, oinstanceid, opartyid, trim(opath) as opath, hinstanceid, hpartyid, trim(hpath) as hpath "
			+ "from F_T_Route where oinstanceid = #{instanceId}")
	public List<Route> getORouteListByInstanceId(String instanceId);
	
	@Select("select route_id, business_id, oinstanceid, opartyid, trim(opath) as opath, hinstanceid, hpartyid, trim(hpath) as hpath "
			+ "from F_T_Route where hinstanceid = #{instanceId}")
	public List<Route> getHRouteListByInstanceId(String instanceId);
		
	
	@Select("select r.route_id, r.business_id, r.oinstanceid, r.opartyid, r.opath, u.user_name as o_user_name from F_T_ROUTE r, F_T_USER u "
			+ "where r.oinstanceid = u.instance_id and r.opartyid = u.party_id "
			+ "and r.route_id = #{routeId}")
	public Route getORouteByRouteId(String routeId);
	
	@Select("select r.route_id, r.business_id, r.hinstanceid, r.hpartyid, r.hpath, u.user_name as h_user_name from F_T_ROUTE r, F_T_USER u "
			+ "where r.hinstanceid = u.instance_id and r.hpartyid = u.party_id "
			+ "and r.route_id = #{routeId}")
	public Route getHRouteByRouteId(String routeId);
}
