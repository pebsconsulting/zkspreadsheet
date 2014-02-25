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

import org.zkoss.zss.ngmodel.NRichText;
/**
 * 
 * @author dennis
 * @since 3.5.0
 */
public class CommentImpl extends AbstractCommentAdv {
	private static final long serialVersionUID = 1L;
	private Object text;
	private String author;
	private boolean visible = true;
	
	@Override
	public String getText() {
		return text instanceof String?(String)text:null;
	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void setRichText(NRichText text) {
		this.text = text;
	}

	@Override
	public NRichText setupRichText() {
		if(this.text instanceof NRichText){
			return (NRichText)this.text;
		}
		this.text = new RichTextImpl();
		return (NRichText)this.text;
	}

	@Override
	public NRichText getRichText() {
		return text instanceof NRichText?(NRichText)text:null;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}
	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public AbstractCommentAdv clone() {
		CommentImpl comment = new CommentImpl();
		comment.setAuthor(author);
		comment.setVisible(visible);
		if(this.text instanceof NRichText){
			comment.setRichText(((AbstractRichTextAdv)text).clone());
		}else if(this.text instanceof String){
			comment.setText((String)text);
		}
		
		
		return comment;
	}

}