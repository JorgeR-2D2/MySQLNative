package com.jrojas.my_sql_nativer.MySQLNative.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class LeftJoin {
	
	public LeftJoin() {
		
	}
	
	public void setTabla(String tabla) {
		this.tabla = tabla;
	}
	
	public void addCondicional(Condicional condicional) {
		condicionales.add(condicional);
	}
	
	public void addCondicion(String tablaRef1, String condicion1,Aritmetico aritmetico ,String tablaRef2 ,String condicion2) {
		condiciones.put(condicion1, condicion2);
		tablasRef.put(tablaRef1, tablaRef2);
		aritmeticos.add(aritmetico);
	}
	
	public String build() {
		StringBuilder construccion=new StringBuilder("LEFT JOIN "+tabla+ " ON ");
		Iterator<Entry<String, String>> iterador=condiciones.entrySet().iterator();
		Iterator<Entry<String, String>> iterador2=tablasRef.entrySet().iterator();
		int contador=0;
		for(int i=0;i<aritmeticos.size();i++) {
			Map.Entry<String,String> pairCondiciones = (Entry<String, String>)iterador.next();
			Map.Entry<String,String> pairTablasRef = (Entry<String, String>)iterador2.next();
			construccion.append(pairTablasRef.getKey()+"."+pairCondiciones.getKey()+" "+aritmeticos.get(contador).getValue()+
					" "+pairTablasRef.getValue()+"."+pairCondiciones.getValue()+" ");
			try {
				construccion.append(condicionales.get(i) + " ");
			}catch(Exception e) {
				
			}
			contador++;
		}
		return construccion.toString();
	}

	private String tabla;
	private List<Condicional> condicionales=new ArrayList<Condicional>();
	private HashMap<String,String> tablasRef=new HashMap<String,String>();
	private HashMap<String,String> condiciones= new HashMap<String,String>();
	private List<Aritmetico> aritmeticos=new ArrayList<Aritmetico>();
}
