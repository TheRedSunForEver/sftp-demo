package com.hp.ecip.service;


public interface UserService {
	boolean setUserAuthAndRoute(String instanceId);
	boolean setUserAuthByUserId(String userId);
	boolean setORouteAuthByRouteId(String routeId);
	boolean setHRouteAuthByRouteId(String routeId);
}
