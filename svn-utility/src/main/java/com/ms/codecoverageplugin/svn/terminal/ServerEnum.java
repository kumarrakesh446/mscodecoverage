/**
 * 
 */
package com.ms.codecoverageplugin.svn.terminal;

/**
 * @author rAk
 *
 */
public enum ServerEnum {
   WINDOW("Window Cmd","WINDOW"), TELNET("Telnet","TELNET"), SSH("Ssh","SSH");;
   
	private String name,value;
	
   ServerEnum(String name,String value){
	   this.setName(name);
	   this.setValue(value);
   }

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getValue() {
	return value;
}

public void setValue(String value) {
	this.value = value;
}
}
