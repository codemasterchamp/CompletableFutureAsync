package com.java.kata4;

public class IndianGod implements Comparable<IndianGod> {
 
	private String name;
	private Integer occurance;
	
	IndianGod(String name, Integer occurance) {
		this.name = name;
		this.occurance = occurance;
	}
	
	public String getName() {
		return name;
	}

	public Integer getOccurance() {
		return occurance;
	}
	
	@Override
	public String toString() {
		return "{" + this.name + ":" 
				   + this.occurance + "}";
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public int compareTo(IndianGod o) {
		return (o.occurance > this.occurance ? 1 : -1);
	}
}
