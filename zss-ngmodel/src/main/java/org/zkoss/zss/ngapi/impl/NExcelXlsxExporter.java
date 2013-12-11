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
package org.zkoss.zss.ngapi.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;

import org.zkoss.poi.ss.usermodel.Cell;
import org.zkoss.poi.ss.usermodel.CellStyle;
import org.zkoss.poi.ss.usermodel.FillPatternType;
import org.zkoss.poi.ss.usermodel.Font;
import org.zkoss.poi.ss.usermodel.HorizontalAlignment;
import org.zkoss.poi.ss.usermodel.Workbook;
import org.zkoss.poi.ss.util.CellRangeAddress;
import org.zkoss.poi.xssf.usermodel.XSSFCell;
import org.zkoss.poi.xssf.usermodel.XSSFCellStyle;
import org.zkoss.poi.xssf.usermodel.XSSFColor;
import org.zkoss.poi.xssf.usermodel.XSSFDataFormat;
import org.zkoss.poi.xssf.usermodel.XSSFFont;
import org.zkoss.poi.xssf.usermodel.XSSFRow;
import org.zkoss.poi.xssf.usermodel.XSSFSheet;
import org.zkoss.poi.xssf.usermodel.XSSFWorkbook;
import org.zkoss.zss.ngapi.NExporter;
import org.zkoss.zss.ngapi.impl.ExcelExportFactory.Type;
import org.zkoss.zss.ngmodel.CellRegion;
import org.zkoss.zss.ngmodel.NBook;
import org.zkoss.zss.ngmodel.NCell;
import org.zkoss.zss.ngmodel.NCell.CellType;
import org.zkoss.zss.ngmodel.NCellStyle;
import org.zkoss.zss.ngmodel.NCellStyle.Alignment;
import org.zkoss.zss.ngmodel.NCellStyle.BorderType;
import org.zkoss.zss.ngmodel.NCellStyle.FillPattern;
import org.zkoss.zss.ngmodel.NCellStyle.VerticalAlignment;
import org.zkoss.zss.ngmodel.NColumn;
import org.zkoss.zss.ngmodel.NFont;
import org.zkoss.zss.ngmodel.NFont.Boldweight;
import org.zkoss.zss.ngmodel.NFont.TypeOffset;
import org.zkoss.zss.ngmodel.NFont.Underline;
import org.zkoss.zss.ngmodel.NRow;
import org.zkoss.zss.ngmodel.NSheet;
/**
 * 
 * @author dennis
 * @since 3.5.0
 */
public class NExcelXlsxExporter extends AbstractExporter{
	
	private XSSFWorkbook workbook;
	private Map<NCellStyle, XSSFCellStyle> styleTable;
	private Map<NFont, XSSFFont> fontTable;
	
	@Override
	public void export(NBook book, File file) throws IOException {
		OutputStream os = null;
		try{
			os = new FileOutputStream(file);
			export(book,os);
		}finally{
			if(os!=null){
				try{
					os.close();
				}catch(Exception x){};
			}
		}
	}
	
	@Override
	public void export(NBook book, OutputStream fos) throws IOException {
		ReadWriteLock lock = book.getBookSeries().getLock();
		lock.writeLock().lock();
		
		styleTable = new HashMap<NCellStyle, XSSFCellStyle>();
		fontTable = new HashMap<NFont, XSSFFont>();
		
		try{
			
			workbook = new XSSFWorkbook();
			// sheet iterator
			for(NSheet sheet : book.getSheets()) {
				
				XSSFSheet xssfSheet = workbook.createSheet(sheet.getSheetName());
				
				// Merge
				for(CellRegion region : sheet.getMergedRegions()) {
					xssfSheet.addMergedRegion(new CellRangeAddress(region.row, region.lastRow, region.column, region.lastColumn));
				}
				
				// TODO charts & picture
//				sheet.getCharts();
//				sheet.getPictures();
				
//				xssfSheet.setDefaultColumnWidth(sheet.getDefaultColumnWidth() / 256);
//				xssfSheet.setDefaultColumnStyle(column, style);
//				xssfSheet.setDefaultRowHeight((short)pxToTwip(sheet.getDefaultRowHeight()));
				
				// row iterator
				Iterator<NRow> rowiter = sheet.getRowIterator();
				while(rowiter.hasNext()) {
					
					NRow row = rowiter.next();
					XSSFRow xssfRow = xssfSheet.createRow(row.getIndex());
					
//					if(row.getHeight() != sheet.getDefaultRowHeight()) {
//						xssfRow.setCustomHeight(true);
//					}
					
//					xssfRow.setHeight((short)pxToTwip(row.getHeight()));
					
					NCellStyle rowStyle = row.getCellStyle();
					XSSFCellStyle xssfRowStyle = toXSSFCellStyle(rowStyle);
					xssfRow.setRowStyle(xssfRowStyle);
					
					// cell iterator
					Iterator<NCell> celliter = row.getCellIterator();
					while(celliter.hasNext()) {
						
						NCell cell = celliter.next();
						XSSFCell xssfCell = xssfRow.createCell(cell.getColumnIndex());
						
						NCellStyle cellStyle = cell.getCellStyle();
						xssfCell.setCellStyle(toXSSFCellStyle(cellStyle));
						
						switch(cell.getType()) {
							case BLANK:
								xssfCell.setCellType(Cell.CELL_TYPE_BLANK);
								break;
							case ERROR:
								xssfCell.setCellType(Cell.CELL_TYPE_ERROR);
								xssfCell.setCellErrorValue(cell.getErrorValue().getCode());
								break;
							case BOOLEAN:
								xssfCell.setCellType(Cell.CELL_TYPE_BOOLEAN);
								xssfCell.setCellValue(cell.getBooleanValue());
								break;
							case FORMULA:
								xssfCell.setCellType(Cell.CELL_TYPE_FORMULA);
								xssfCell.setCellFormula(cell.getFormulaValue());
								break;
							case NUMBER:
								xssfCell.setCellType(Cell.CELL_TYPE_NUMERIC);
								xssfCell.setCellValue((Double)cell.getNumberValue());
								break;
							case STRING:
								xssfCell.setCellType(Cell.CELL_TYPE_STRING);
								xssfCell.setCellValue(cell.getStringValue());
								break;
							default:
						}

					} // End Cell
				} // End Row
				
				// column iterator
				Iterator<NColumn> coliter = sheet.getColumnIterator();
				while(coliter.hasNext()) {
					NColumn column = coliter.next();
//					xssfSheet.setColumnWidth(column.getIndex(), column.getWidth());
//					xssfSheet.setColumnStyle(column.getIndex() ,toXSSFCellStyle(column.getCellStyle()));
				}
				
			} // End Sheet
			
			workbook.write(fos);
			
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public static int pxToTwip(int px) {
		return px * 72 * 20 / 96; //assume 96dpi
	}

	private XSSFCellStyle toXSSFCellStyle(NCellStyle cellStyle) {
		
		// instead of creating a new style, use old one if exist
		XSSFCellStyle xssfCellStyle = styleTable.get(cellStyle);
		if(xssfCellStyle != null) {
			return xssfCellStyle;
		}
		
		xssfCellStyle = workbook.createCellStyle();
		
		xssfCellStyle.setAlignment(toPOIAlignment(cellStyle.getAlignment()));
		xssfCellStyle.setBorderBottom(toPOIBorderType(cellStyle.getBorderBottom()));
		xssfCellStyle.setBottomBorderColor(new XSSFColor(cellStyle.getBorderBottomColor().getRGB()));
		xssfCellStyle.setBorderLeft(toPOIBorderType(cellStyle.getBorderLeft()));
		xssfCellStyle.setLeftBorderColor(new XSSFColor(cellStyle.getBorderLeftColor().getRGB()));
		xssfCellStyle.setBorderRight(toPOIBorderType(cellStyle.getBorderRight()));
		xssfCellStyle.setRightBorderColor(new XSSFColor(cellStyle.getBorderRightColor().getRGB()));
		xssfCellStyle.setBorderTop(toPOIBorderType(cellStyle.getBorderTop()));
		xssfCellStyle.setTopBorderColor(new XSSFColor(cellStyle.getBorderTopColor().getRGB()));
		xssfCellStyle.setFillForegroundColor(new XSSFColor(cellStyle.getFillColor().getRGB()));
		xssfCellStyle.setFillPattern(toPOIFillPattern(cellStyle.getFillPattern()));
		xssfCellStyle.setVerticalAlignment(toPOIVerticalAlignment(cellStyle.getVerticalAlignment()));
		
		// refer from BookHelper.setDataFormat
		XSSFDataFormat df = workbook.createDataFormat();
		short fmt = df.getFormat(cellStyle.getDataFormat());
		xssfCellStyle.setDataFormat(fmt);
		
		// font
		xssfCellStyle.setFont(toXSSFFont(cellStyle.getFont()));
		
		// put into table
		styleTable.put(cellStyle, xssfCellStyle);
		
		return xssfCellStyle;
	}
	
	private XSSFFont toXSSFFont(NFont font) {

		XSSFFont xssfFont = fontTable.get(font);
		if(xssfFont != null) {
			return xssfFont;
		}
		
		xssfFont = workbook.createFont();
		xssfFont.setBoldweight(toPOIBoldweight(font.getBoldweight()));
		// FIXME is POI color reuseable?
		xssfFont.setColor(new XSSFColor(font.getColor().getRGB()));
		xssfFont.setFontHeight((double)font.getHeightPoints()/20);
		xssfFont.setFontName(font.getName());
		xssfFont.setTypeOffset(toPOITypeOffset(font.getTypeOffset()));
		xssfFont.setUnderline(toPOIUnderline(font.getUnderline()));
		
		// put into table
		fontTable.put(font, xssfFont);
		
		return xssfFont;
	}
	
	private org.zkoss.poi.ss.usermodel.VerticalAlignment toPOIVerticalAlignment(VerticalAlignment vAlignment) {
		
		switch(vAlignment) {
			case BOTTOM:
				return org.zkoss.poi.ss.usermodel.VerticalAlignment.BOTTOM;
			case CENTER:
				return org.zkoss.poi.ss.usermodel.VerticalAlignment.CENTER;
			case JUSTIFY:
				return org.zkoss.poi.ss.usermodel.VerticalAlignment.JUSTIFY;
			case TOP:
			default:
				return org.zkoss.poi.ss.usermodel.VerticalAlignment.TOP;
		}
	}
	
	private FillPatternType toPOIFillPattern(FillPattern fillPattern) {
		switch(fillPattern) {
		case ALT_BARS:
			return FillPatternType.ALT_BARS;
		case BIG_SPOTS:
			return FillPatternType.BIG_SPOTS;
		case BRICKS:
			return FillPatternType.BRICKS;
		case DIAMONDS:
			return FillPatternType.DIAMONDS;
		case FINE_DOTS:
			return FillPatternType.FINE_DOTS;
		case LEAST_DOTS:
			return FillPatternType.LEAST_DOTS;
		case LESS_DOTS:
			return FillPatternType.LESS_DOTS;
		case SOLID_FOREGROUND:
			return FillPatternType.SOLID_FOREGROUND;
		case SPARSE_DOTS:
			return FillPatternType.SPARSE_DOTS;
		case SQUARES:
			return FillPatternType.SQUARES;
		case THICK_BACKWARD_DIAG:
			return FillPatternType.THICK_BACKWARD_DIAG;
		case THICK_FORWARD_DIAG:
			return FillPatternType.THICK_FORWARD_DIAG;
		case THICK_HORZ_BANDS:
			return FillPatternType.THICK_HORZ_BANDS;
		case THICK_VERT_BANDS:
			return FillPatternType.THICK_VERT_BANDS;
		case THIN_BACKWARD_DIAG:
			return FillPatternType.THIN_BACKWARD_DIAG;
		case THIN_FORWARD_DIAG:
			return FillPatternType.THIN_FORWARD_DIAG;
		case THIN_HORZ_BANDS:
			return FillPatternType.THIN_HORZ_BANDS;
		case THIN_VERT_BANDS:
			return FillPatternType.THIN_VERT_BANDS;
		case NO_FILL:
		default:
			return FillPatternType.NO_FILL;
		}
	}
	
	private byte toPOIBorderType(BorderType borderType) {
		switch(borderType) {
		case DASH_DOT:
			return CellStyle.BORDER_DASH_DOT;
		case DASHED:
			return CellStyle.BORDER_DASHED;
		case DOTTED:
			return CellStyle.BORDER_DOTTED;
		case DOUBLE:
			return CellStyle.BORDER_DOUBLE;
		case HAIR:
			return CellStyle.BORDER_HAIR;
		case MEDIUM:
			return CellStyle.BORDER_MEDIUM;
		case MEDIUM_DASH_DOT:
			return CellStyle.BORDER_DASH_DOT;
		case MEDIUM_DASH_DOT_DOT:
			return CellStyle.BORDER_DASH_DOT_DOT;
		case MEDIUM_DASHED:
			return CellStyle.BORDER_MEDIUM_DASHED;
		case SLANTED_DASH_DOT:
			return CellStyle.BORDER_SLANTED_DASH_DOT;
		case THICK:
			return CellStyle.BORDER_THICK;
		case THIN:
			return CellStyle.BORDER_THIN;
		case DASH_DOT_DOT:
			return CellStyle.BORDER_DASH_DOT_DOT;
		case NONE:
		default:
			return CellStyle.BORDER_NONE;
		}
	}
	
	private HorizontalAlignment toPOIAlignment(Alignment alignment) {
		switch(alignment) {
		case CENTER:
			return HorizontalAlignment.CENTER;
		case FILL:
			return HorizontalAlignment.FILL;
		case JUSTIFY:
			return HorizontalAlignment.JUSTIFY;
		case RIGHT:
			return HorizontalAlignment.RIGHT;
		case LEFT:
			return HorizontalAlignment.LEFT;
		case CENTER_SELECTION:
			return HorizontalAlignment.CENTER_SELECTION;
		case GENERAL:
			default:
			return HorizontalAlignment.GENERAL;
		}
	}
	
	private short toPOIBoldweight(Boldweight bold) {
		switch(bold) {
			case BOLD:
				return Font.BOLDWEIGHT_BOLD;
			case NORMAL:
			default:
				return Font.BOLDWEIGHT_NORMAL;
		}
	}
	
	private short toPOITypeOffset(TypeOffset typeOffset) {
		switch(typeOffset) {
			case SUPER:
				return Font.SS_SUPER;
			case SUB:
				return Font.SS_SUB;
			case NONE:
			default:
				return Font.SS_NONE;
		}
	}
	
	private byte toPOIUnderline(Underline underline) {
		switch(underline) {
			case SINGLE:
				return Font.U_SINGLE;
			case DOUBLE:
				return Font.U_DOUBLE;
			case DOUBLE_ACCOUNTING:
				return Font.U_DOUBLE_ACCOUNTING;
			case SINGLE_ACCOUNTING:
				return Font.U_SINGLE_ACCOUNTING;
			case NONE:
			default:
				return Font.U_NONE;
		}
	}

}
