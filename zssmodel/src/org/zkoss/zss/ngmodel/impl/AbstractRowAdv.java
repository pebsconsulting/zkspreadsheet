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
import java.util.Iterator;

import org.zkoss.zss.ngmodel.NCell;
import org.zkoss.zss.ngmodel.NCellStyle;
import org.zkoss.zss.ngmodel.NRow;
/**
 * 
 * @author dennis
 * @since 3.5.0
 */
public abstract class AbstractRowAdv implements NRow,LinkedModelObject,Serializable{
	private static final long serialVersionUID = 1L;

	/*package*/ abstract NCellStyle getCellStyle(boolean local);
	
	/*package*/ abstract AbstractCellAdv getCell(int columnIdx, boolean proxy);

	/*package*/ abstract AbstractCellAdv getOrCreateCell(int columnIdx);
	
//	/*package*/ abstract void onModelInternalEvent(ModelInternalEvent event);

	/*package*/ abstract int getStartCellIndex();
	/*package*/ abstract int getEndCellIndex();
	
	/*package*/ abstract void clearCell(int start, int end);

	/*package*/ abstract void insertCell(int start, int size);

	/*package*/ abstract void deleteCell(int start, int size);
	
	/*package*/ abstract Iterator<AbstractCellAdv> getCellIterator(boolean reverse);

	/*package*/ abstract void setIndex(int newidx);

	/*package*/ abstract void moveCellTo(AbstractRowAdv target, int start, int end, int offset);
}