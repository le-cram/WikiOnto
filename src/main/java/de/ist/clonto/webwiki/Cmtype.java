/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.webwiki;

/**
 * 
 * @author dmosen@uni-koblenz.de
 *
 */
public enum Cmtype {
	DEFAULT(""), SUBCAT("subcat"), PAGE("page"), FILE("file");

	public final String id;

	Cmtype(String id) {
		this.id = id;
	}
}
