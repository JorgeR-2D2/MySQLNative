package com.jrojas.my_sql_nativer.MySQLNative.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;



public class MySQLNative<T> {

	
	public MySQLNative(T object) {
		t=object;
	}

	public MySQLNative(T object,String queryInicial) {
		t=object;
		this.queryInicial = queryInicial;
	}

	public void setSelector(Selector selector) {
		this.selector = selector;
	}

	public void setModificador(Modificador modificador) {
		this.modificador = modificador;
	}

	public void addSelect(String tabla, String campo) {
		selects.add(tabla+"."+campo);
	}

	public void setFrom(String tabla) {
		from = tabla;
	}

	public void addWhere(String tablaRef1, String condicion1, Aritmetico aritmetico, String tablaRef2,
			String condicion2) {
		wheres.put(tablaRef1 + "." + condicion1, tablaRef2 + "." + condicion2);
		aritmeticos.add(aritmetico);
	}
	
	public void addWhere(String tablaRef1, String condicion1, Aritmetico aritmetico,
			String condicion2) {
		wheres.put(tablaRef1 + "." + condicion1, condicion2);
		aritmeticos.add(aritmetico);
	}
	
	public void addWhere(String tablaRef1, String condicion1, Aritmetico aritmetico,
			Integer condicion2) {
		wheres.put(tablaRef1 + "." + condicion1, ""+condicion2);
		aritmeticos.add(aritmetico);
	}

	public void addCondicional(Condicional condicional) {
		condicionales.add(condicional);
	}

	public void addOrderBy(String tabla,String campo) {
		orderBy.put(tabla+"."+campo, Orden.ASC);
	}

	public void addOrderBy(String tabla, String campo, Orden orden) {
		orderBy.put(tabla + "." + campo, orden);
	}

	public void addLeftJoin(LeftJoin leftJoin) {
		leftJoins.add(leftJoin);
	}

	public String build() {

		if(selector==null) {
			selector=Selector.SELECT;
		}
		StringBuilder construccion = new StringBuilder();
		StringBuilder construccionSelects = new StringBuilder();
		StringBuilder construccionWheres = new StringBuilder();
		StringBuilder construccionOrdersBy = new StringBuilder();

		Iterator<Entry<String, String>> iteradorWheres = wheres.entrySet().iterator();
		Iterator<Entry<String, Orden>> iteradorOrdersBy = orderBy.entrySet().iterator();

		int contador = 0;
		int contadorSelects = 0;
		int contadorWheres = 0;
		int contadorOrderBy = 0;

		while (contador<selects.size() || iteradorWheres.hasNext() || iteradorOrdersBy.hasNext()) {

			if(contador<selects.size()) {
				if(contadorSelects==0) {
					if(modificador!=null) {
						if(contador!=selects.size()-1) {
							construccionSelects.append(selector + " " +modificador+" ("+ selects.get(contador)+",");
						}
						else {
							construccionSelects.append(selector + " " +modificador+" ("+ selects.get(contador)+") FROM ");
						}
					}else {
						if(contador!=selects.size()-1) {
							construccionSelects.append(selector + " " + selects.get(contador)+",");
						}
						else {
							construccionSelects.append(selector + " " + selects.get(contador)+" FROM ");
						}
					}
				}else {
					if(contador!=selects.size()-1) {
						construccionSelects.append(selects.get(contador)+",");
					}
					else {
						if(modificador!=null) {
							construccionSelects.append(selects.get(contador)+") FROM ");
						}else {
							construccionSelects.append(selects.get(contador)+" FROM ");
						}
						
					}
				}
				contadorSelects++;
			}
			
			if (iteradorWheres.hasNext()) {
				contadorWheres++;
				Map.Entry<String, String> pairWhere = (Entry<String, String>) iteradorWheres.next();
				if (contadorWheres == 1) {
					construccionWheres.append("WHERE " + pairWhere.getKey() + " " + aritmeticos.get(contador).getValue() + " "
							+ pairWhere.getValue() + " ");
				} else {
					construccionWheres.append(
							pairWhere.getKey() + " " + aritmeticos.get(contador).getValue() + " " + pairWhere.getValue() + " ");
				}
				try {
					construccionWheres.append(condicionales.get(contador) + " ");
				} catch (Exception e) {

				}

			}
			if (iteradorOrdersBy.hasNext()) {
				contadorOrderBy++;
				Map.Entry<String, Orden> pairOrderBy = (Entry<String, Orden>) iteradorOrdersBy.next();
				if (contadorOrderBy == 1) {
					if (iteradorOrdersBy.hasNext()) {
						construccionOrdersBy
								.append("ORDER BY " + pairOrderBy.getKey() + " " + pairOrderBy.getValue() + ",");
					} else {
						construccionOrdersBy
								.append("ORDER BY " + pairOrderBy.getKey() + " " + pairOrderBy.getValue() + " ");
					}

				} else {
					if (iteradorOrdersBy.hasNext()) {
						construccionOrdersBy.append(pairOrderBy.getKey() + " " + pairOrderBy.getValue() + ",");
					} else {
						construccionOrdersBy.append(pairOrderBy.getKey() + " " + pairOrderBy.getValue() + " ");
					}
				}

			}
			contador++;
		}

		construccion.append(construccionSelects.toString()+from+" ");
		if(leftJoins.size()!=0) {
			for(LeftJoin lf:leftJoins) {
				construccion.append(lf.build()+" ");
			}
		}
		construccion.append(construccionWheres.toString()+construccionOrdersBy);
		return construccion.toString()+t.getClass();

	}
	public List<T> getResults() {
		Query q=em.createNativeQuery(this.build(), t.getClass());
		return q.getResultList();
	}
	
	
	private String queryInicial;

	private Selector selector;
	private Modificador modificador;
	private List<String> selects=new ArrayList<String>();
	private String from;
	private HashMap<String, String> wheres=new HashMap<String,String>();
	private HashMap<String, Orden> orderBy=new HashMap<String,Orden>();
	private List<Condicional> condicionales=new ArrayList<Condicional>();
	private List<Aritmetico> aritmeticos=new ArrayList<Aritmetico>();
	private List<LeftJoin> leftJoins = new ArrayList<LeftJoin>();
	private Class<T> ct;
	private T t;
	@Autowired
	EntityManager em;
}
