package cn.benjamin.loxia.struts2.table;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.benjamin.loxia.support.excel.ExcelWriter;
import cn.benjamin.loxia.support.excel.definition.ExcelBlock;
import cn.benjamin.loxia.support.excel.definition.ExcelCell;
import cn.benjamin.loxia.support.excel.definition.ExcelCellConditionStyle;
import cn.benjamin.loxia.support.excel.definition.ExcelManipulatorDefinition;
import cn.benjamin.loxia.support.excel.definition.ExcelSheet;
import cn.benjamin.loxia.support.excel.impl.DefaultExcelWriter;
import cn.benjamin.loxia.utils.ClassLoaderUtils;

public class TableModelUtils {
	
	private static final String EXCEL_TEMPLATE = "exceltemplate/table.xls";
	
	private static final String TITLE_CELL_INDEX = "C1";
	private static final String LABEL_CELL_INDEX = "B2";
	private static final String COLUMN_CELL_INDEX = "B3";
	
	/**
	 * Export data to Excel with given template
	 * @param os
	 * @param is
	 * @param tableModel
	 * @param showAll
	 */
	public static void outputExcel(OutputStream os, InputStream is, TableModel tableModel, boolean showAll){
		assert is != null : "Input template is not found";
		ExcelManipulatorDefinition definition = generateDefinition(tableModel);
		ExcelWriter excelWriter = new DefaultExcelWriter();
		excelWriter.setDefinition(definition);
		
		Map<String,Object> beans = new HashMap<String, Object>();
		beans.put("title", tableModel.getModelName());
		for(int i=0; i< tableModel.getColumnNames().length; i++){
			beans.put("__column" + (i+1), tableModel.getColumnNames()[i]);		
		}
		beans.put("__columns", tableModel.getItems());
		excelWriter.write(is, os, beans);
	}
	
	/**
	 * Export data to Excel with default template
	 * @param os
	 * @param tableModel
	 * @param showAll
	 */
	public static void outputExcel(OutputStream os, TableModel tableModel, boolean showAll){
		outputExcel(os, 
				ClassLoaderUtils.getResourceAsStream(EXCEL_TEMPLATE, TableModelUtils.class),
				tableModel, showAll);
	}
	
	public static void outputExcel(OutputStream os, TableModel tableModel){
		outputExcel(os, tableModel, true);
	}
	
	private static void setDefaultStyle(ExcelCell cell){
		setDefaultStyle(cell, null);
	}
	
	private static void setDefaultStyle(ExcelCell cell, String cellStyle){
		ExcelCellConditionStyle cstyle = new ExcelCellConditionStyle();
		cstyle.setCellIndex(cellStyle == null? COLUMN_CELL_INDEX : cellStyle);
		cstyle.setCondition("true");		
		cell.addStyle(cstyle);
	}
	
	private static ExcelManipulatorDefinition generateDefinition(TableModel tableModel){
		ExcelManipulatorDefinition definition = new ExcelManipulatorDefinition();
		definition.setStyleSheetPosition(1);
		List<ExcelSheet> excelSheets = new ArrayList<ExcelSheet>();
		ExcelSheet excelSheet = new ExcelSheet();
		excelSheet.setDisplayName(tableModel.getModelName());
		//add Head Block
		ExcelBlock headBlock = new ExcelBlock();
		
		headBlock.setStartRow(0);
		headBlock.setStartCol(0);
		headBlock.setEndRow(1);
		headBlock.setEndCol(tableModel.getColumnNames().length);
		
		ExcelCell titleCell = new ExcelCell();
		titleCell.setCellIndex("C1");
		titleCell.setDataName("title");		
		setDefaultStyle(titleCell, TITLE_CELL_INDEX);		
		headBlock.addCell(titleCell);
		for(int i=0; i< tableModel.getColumnNames().length; i++){
			ExcelCell labelCell = new ExcelCell();
			labelCell.setRow(1);
			labelCell.setCol(i+1);
			labelCell.setDataName("__column" + (i+1));
			setDefaultStyle(labelCell, LABEL_CELL_INDEX);
			headBlock.addCell(labelCell);
		}
		
		excelSheet.addExcelBlock(headBlock);
		
		//add Body Block
		ExcelBlock bodyBlock = new ExcelBlock();
		bodyBlock.setStartRow(2);
		bodyBlock.setStartCol(1);
		bodyBlock.setEndRow(2);
		bodyBlock.setEndCol(tableModel.getColumnNames().length);
		bodyBlock.setLoop(true);
		bodyBlock.setDataName("__columns");
		for(int i=0; i< tableModel.getColumnNames().length; i++){
			ExcelCell cell = new ExcelCell();
			cell.setRow(2);
			cell.setCol(i+1);
			cell.setDataName(tableModel.getColumns()[i]);
			setDefaultStyle(cell);
			bodyBlock.addCell(cell);
		}
		
		excelSheet.addExcelBlock(bodyBlock);
		
		excelSheets.add(excelSheet);
		definition.setExcelSheets(excelSheets);
		return definition;
	}
}
