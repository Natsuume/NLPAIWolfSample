package org.aiwolf.sample.nl;

public class Protocol {
	private String japanese;
	private String protocol;
	private ProtocolType type;
	public Protocol(String line){
		String[] array = line.split(",");
		this.japanese = array.length > 0 ? array[0] : null;
		this.type = array.length > 1 ? ProtocolType.valueOf(array[1]) : null;
		this.protocol = array.length > 2 ? array[2] : null;
	}
	public String getJapanese() {
		return japanese;
	}
	public String getProtocol() {
		return protocol;
	}
	public ProtocolType getType() {
		return type;
	}

	public String toString(){
		return japanese+","+type+","+protocol;
	}
}
