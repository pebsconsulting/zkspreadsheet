/*

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2013/12/01 , Created by dennis
}}IS_NOTE

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zss.ngmodel.impl;

import java.io.Serializable;

import org.zkoss.zss.ngmodel.NHyperlink;
/**
 * 
 * @author dennis
 * @since 3.5.0
 */
public abstract class AbstractHyperlinkAdv implements NHyperlink,Serializable{
	private static final long serialVersionUID = 1L;

	public abstract AbstractHyperlinkAdv clone();

}
