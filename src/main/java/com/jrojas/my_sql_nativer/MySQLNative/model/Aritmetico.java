package com.jrojas.my_sql_nativer.MySQLNative.model;

public enum Aritmetico {
	EQUAL("="),NOTEQUAL("!="),MORE(">"),LESS("<"),MOREOREQUAL(">="),LESSOREQUAL("<=");
	
	
	private final String value;
	
	private Aritmetico(String value){
		this.value=value;
	}

	public String getValue() {
		return value;
	}
	
}
