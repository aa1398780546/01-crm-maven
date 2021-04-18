package com.bjpowernode.crm.utils;

public class ServiceFactory {

	//代理方法获得Service
	public static Object getService(Object service){
		return new TransactionInvocationHandler(service).getProxy();
	}
	
}
