package loxia.support.excel.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loxia.support.LoxiaSupportConstants;
import loxia.support.LoxiaSupportSettings;
import loxia.support.excel.ExcelUtil;
import loxia.support.excel.ExcelWriter;
import loxia.support.excel.WriteStatus;
import loxia.support.excel.definition.ExcelBlock;
import loxia.support.excel.definition.ExcelCell;
import loxia.support.excel.definition.ExcelCellConditionStyle;
import loxia.support.excel.definition.ExcelManipulatorDefinition;
import loxia.support.excel.definition.ExcelSheet;
import loxia.utils.OgnlStack;

public class DefaultExcelWriter implements ExcelWriter, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1482207293275885449L;

	static final Logger logger = LoggerFactory.getLogger(DefaultExcelWriter.class);
	
	private ExcelManipulatorDefinition definition;

	public WriteStatus write(InputStream is, OutputStream os, Map<String, Object> beans) {
		WriteStatus writeStatus = new DefaultWriteStatus();
		writeStatus.setStatus(WriteStatus.STATUS_SUCCESS);
		
		Workbook wb = null;
		try {
			wb = WorkbookFactory.create(is);
		} catch (IOException e) {
			writeStatus.setStatus(WriteStatus.STATUS_READ_TEMPLATE_FILE_ERROR);
		} catch (InvalidFormatException e) {
			writeStatus.setStatus(WriteStatus.STATUS_READ_TEMPLATE_FILE_ERROR);
		}
		
		if(wb != null){			
			if(definition.getExcelSheets().size() == 0 ||
					wb.getNumberOfSheets() < definition.getExcelSheets().size()){
				writeStatus.setStatus(WriteStatus.STATUS_SETTING_ERROR);
				writeStatus.setMessage("No sheet definition found or Sheet Number in definition is more than number in template file.");			
			}else{
				Map<String, CellStyle> styleMap = new HashMap<String, CellStyle>();
				if(definition.getStyleSheetPosition() != null){
					if(definition.getStyleSheetPosition().intValue() < definition.getExcelSheets().size()){
						writeStatus.setStatus(WriteStatus.STATUS_SETTING_ERROR);
						writeStatus.setMessage("Style Sheet can not be one Template Sheet.");
						return writeStatus;
					}
					for(int i=0; i< definition.getExcelSheets().size(); i++){						
						initConditionalStyle(wb.getSheetAt(definition.getStyleSheetPosition()), 
								definition.getExcelSheets().get(i), 
								styleMap);
					}		
					wb.removeSheetAt(definition.getStyleSheetPosition());
					logger.debug("{} styles found", styleMap.keySet().size());
				}				
				for(int i=0; i< definition.getExcelSheets().size(); i++){
					writeSheet(wb.getSheetAt(i), 
							definition.getExcelSheets().get(i), 
							new OgnlStack(beans), styleMap, writeStatus);
				}
				reCalculateWorkbook(wb);
				wb.setActiveSheet(0);
				try {
					wb.write(os);
				} catch (IOException e) {
					writeStatus.setStatus(WriteStatus.STATUS_WRITE_FILE_ERROR);
				}
			}
		}
		return writeStatus;
	}
	
	public WriteStatus write(String template, OutputStream os, Map<String, Object> beans){
		return write(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(template),os,beans);
	}

	public WriteStatus writePerSheet(InputStream is, OutputStream os, List<Map<String, Object>> beansList) {
		WriteStatus writeStatus = new DefaultWriteStatus();
		writeStatus.setStatus(WriteStatus.STATUS_SUCCESS);
		
		Workbook wb = null;
		try {
			wb = WorkbookFactory.create(is);
		} catch (IOException e) {
			writeStatus.setStatus(WriteStatus.STATUS_READ_TEMPLATE_FILE_ERROR);
		} catch (InvalidFormatException e) {
			writeStatus.setStatus(WriteStatus.STATUS_READ_TEMPLATE_FILE_ERROR);
		}
		
		if(wb != null){
			if(definition.getExcelSheets().size() == 0 ||
					wb.getNumberOfSheets() < 1){
				writeStatus.setStatus(WriteStatus.STATUS_SETTING_ERROR);
				writeStatus.setMessage("No sheet definition found or template file contains no sheet.");			
			}else{
				Map<String, CellStyle> styleMap = new HashMap<String, CellStyle>();
				if(definition.getStyleSheetPosition() != null){
					if(definition.getStyleSheetPosition().intValue() < definition.getExcelSheets().size()){
						writeStatus.setStatus(WriteStatus.STATUS_SETTING_ERROR);
						writeStatus.setMessage("Style Sheet can not be one Template Sheet.");
						return writeStatus;
					}
					initConditionalStyle(wb.getSheetAt(definition.getStyleSheetPosition()), 
							definition.getExcelSheets().get(0), 
							styleMap);	
					wb.removeSheetAt(definition.getStyleSheetPosition());
				}
				//remove sheets except the first one
				for(int i=wb.getNumberOfSheets() -1 ; i > 0; i--){
					wb.removeSheetAt(i);			
				}
				for(int i=0; i< beansList.size(); i++){
					Sheet newSheet = wb.createSheet("Auto Generated Sheet " + i);
					ExcelUtil.copySheet(wb.getSheetAt(0), newSheet);
				writeSheet(newSheet, 
							definition.getExcelSheets().iterator().next(), 
							new OgnlStack(beansList.get(i)), styleMap, writeStatus);
				}
				//remove template sheet
				wb.removeSheetAt(0);
				reCalculateWorkbook(wb);
				wb.setActiveSheet(0);
				try {
					wb.write(os);
				} catch (IOException e) {
					writeStatus.setStatus(WriteStatus.STATUS_WRITE_FILE_ERROR);
				}
			}
		}
		return writeStatus;
	}
	
	public WriteStatus writePerSheet(String template, OutputStream os, List<Map<String, Object>> beansList){
		return writePerSheet(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(template), os, beansList);
	}
	
	private void initConditionalStyle(Sheet styleSheet, 
			ExcelSheet sheetDefinition, Map<String, CellStyle> styleMap){
		for(ExcelBlock blockDefinition: sheetDefinition.getExcelBlocks()){
			initConditionalStyle(styleSheet, blockDefinition, styleMap);
			if(blockDefinition.getChildBlock() != null)
				initConditionalStyle(styleSheet, blockDefinition.getChildBlock(), styleMap);
		}
	}
	
	private void initConditionalStyle(Sheet styleSheet, 
			ExcelBlock blockDefinition, Map<String, CellStyle> styleMap){
		for(ExcelCellConditionStyle style: blockDefinition.getStyles())
			initConditionalStyle(styleSheet, style, styleMap);
		for(ExcelCell cellDefinition: blockDefinition.getCells()){
			for(ExcelCellConditionStyle style: cellDefinition.getStyles()){		
				initConditionalStyle(styleSheet, style, styleMap);
			}
		}
	}
	private void initConditionalStyle(Sheet styleSheet, 
			ExcelCellConditionStyle style, Map<String, CellStyle> styleMap){
		//ignore existed style
		if(styleMap.containsKey(style.getCellIndex())) return;
		int[] position = ExcelUtil.getCellPosition(style.getCellIndex());
		Row row = styleSheet.getRow(position[0]);
		if(row == null) return;
		Cell cell = row.getCell(position[1]);
		if(cell == null) return;
		styleMap.put(style.getCellIndex(), cell.getCellStyle());
		if(logger.isDebugEnabled()){
			logger.debug("Condition Style [{}]", style.getCellIndex());
		}
	}
	
	private void reCalculateWorkbook(Workbook wb){
		FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
		for(int sheetNum = 0; sheetNum < wb.getNumberOfSheets(); sheetNum++) {
		    Sheet sheet = wb.getSheetAt(sheetNum);
		    for(Row r : sheet) {
		        for(Cell c : r) {
		            if(c.getCellType() == Cell.CELL_TYPE_FORMULA) {
		                evaluator.evaluateFormulaCell(c);
		            }
		        }
		    }
		}
	}
	
	private void writeSheet(Sheet sheet, ExcelSheet sheetDefinition, 
			OgnlStack stack, Map<String, CellStyle> styleMap, 
			WriteStatus writeStatus){
		Workbook wb = sheet.getWorkbook();
		String sheetName = sheetDefinition.getDisplayName();		
		if(stack.getValue("sheetName") != null)
			sheetName = (String)stack.getValue("sheetName");
		if(sheetName != null) wb.setSheetName(wb.getSheetIndex(sheet), sheetName);
		
		Map<ExcelBlock, List<CellRangeAddress>> mergedRegions = new HashMap<ExcelBlock, List<CellRangeAddress>>();
		for(int i=0; i< sheet.getNumMergedRegions(); i++){
			CellRangeAddress cra = sheet.getMergedRegion(i);
			for(ExcelBlock blockDefinition: sheetDefinition.getSortedExcelBlocks()){
				if(cra.getFirstRow() >= blockDefinition.getStartRow() &&
						cra.getFirstColumn() >= blockDefinition.getStartCol() &&
						cra.getLastRow() <= blockDefinition.getEndRow() &&
						cra.getLastColumn() <= blockDefinition.getEndCol()){
					List<CellRangeAddress> cras = mergedRegions.get(blockDefinition);
					if(cras == null){
						cras = new ArrayList<CellRangeAddress>();
						mergedRegions.put(blockDefinition, cras);						
					}
					cras.add(cra);
				}
			}
		}
		for(ExcelBlock blockDefinition: sheetDefinition.getSortedExcelBlocks()){
			if(blockDefinition.isLoop()){ 
				writeLoopBlock(sheet, blockDefinition, stack, mergedRegions.get(blockDefinition), styleMap, writeStatus);
			}else{
				writeSimpleBlock(sheet, blockDefinition, stack, styleMap, writeStatus);
			}
		}
	}
	
	private void writeSimpleBlock(Sheet sheet, ExcelBlock blockDefinition,
			OgnlStack stack, Map<String, CellStyle> styleMap, WriteStatus writeStatus){
		//block style
		if(styleMap.keySet().size() > 0){
			for(ExcelCellConditionStyle style: blockDefinition.getStyles()){
				Object obj = stack.getValue(style.getCondition());
				if(obj == null || !(obj instanceof Boolean))
					continue;
				if(((Boolean)obj).booleanValue()){
					setBlockStyle(sheet, style.getStartRow(), style.getEndRow(),
							style.getStartCol(), style.getEndCol(),
							styleMap.get(style.getCellIndex()));
				}
			}
		}
		for(ExcelCell cellDefinition: blockDefinition.getCells()){			
			setCellValue(sheet, cellDefinition.getRow(), cellDefinition.getCol(), 
					cellDefinition.getDataExpr() == null? cellDefinition.getDataName() : cellDefinition.getDataExpr(), stack);
			if(styleMap.keySet().size() > 0){
				for(ExcelCellConditionStyle style: cellDefinition.getStyles()){
					Object obj = stack.getValue(style.getCondition());
					if(obj == null || !(obj instanceof Boolean))
						continue;
					if(((Boolean)obj).booleanValue()){
						setCellStyle(sheet, cellDefinition.getRow(), cellDefinition.getCol(),
								styleMap.get(style.getCellIndex()));
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void writeLoopBlock(Sheet sheet, ExcelBlock blockDefinition,
			OgnlStack stack, List<CellRangeAddress> mergedRegions, 
			Map<String, CellStyle> styleMap, WriteStatus writeStatus){
		try {			
			Object value = stack.getValue(blockDefinition.getDataName());
			if(value == null) return;
			Collection<? extends Object> listValue;
			if(!(value instanceof Collection)){
				if(value.getClass().isArray())
					listValue = Arrays.asList(value);
				else{
					ArrayList<Object> list = new ArrayList<Object>();
					list.add(value);
					listValue = list;
				}
			}else
				listValue = (Collection<? extends Object>)value;
			
			int step = 1;
			Object preObj = null;
			for(Object obj: listValue){
				stack.push(obj);
				stack.addContext("preLine", preObj);
				stack.addContext("lineNum", step -1);
				//shiftrow and prepare write new row
				int nextStartRow = blockDefinition.getStartRow() + step * (blockDefinition.getEndRow() - blockDefinition.getStartRow() + 1);
				if(nextStartRow <= sheet.getLastRowNum())
					sheet.shiftRows(nextStartRow, 
							sheet.getLastRowNum(), blockDefinition.getEndRow() - blockDefinition.getStartRow() + 1, 
							true, false);
					

				writeRow(sheet, blockDefinition, stack, step * (blockDefinition.getEndRow() - blockDefinition.getStartRow() + 1), 
							mergedRegions, styleMap, writeStatus);
				step ++;
				preObj = stack.pop();
			}
			stack.removeContext("preLine");
			stack.removeContext("lineNum");
			
			//delete style sheet
			//if no data, just remove the dummy data
			if(listValue.size() == 0){
				for(int i = blockDefinition.getEndRow(); i>= blockDefinition.getStartRow(); i--)
					sheet.removeRow(sheet.getRow(i));
			}else
				sheet.shiftRows(blockDefinition.getEndRow() + 1, sheet.getLastRowNum(), 
						blockDefinition.getStartRow() - blockDefinition.getEndRow() - 1, true, true);
		} catch (Exception e) {
			e.printStackTrace();
			//do nothing
		}
	}
	
	@SuppressWarnings("unchecked")
	private void writeRow(Sheet sheet, ExcelBlock blockDefinition, OgnlStack stack, int rowOffset,
			List<CellRangeAddress> mergedRegions, 
			Map<String,CellStyle> styleMap, WriteStatus writeStatus) throws Exception{
		if(rowOffset > 0)
			ExcelUtil.copyBlock(sheet, blockDefinition.getStartRow(), blockDefinition.getStartCol(), 
				blockDefinition.getEndRow(), blockDefinition.getEndCol(), true, 
				rowOffset, 0, mergedRegions);
		
		if(styleMap.keySet().size() > 0){
			for(ExcelCellConditionStyle style: blockDefinition.getStyles()){
				Object obj = stack.getValue(style.getCondition());
				if(obj == null || !(obj instanceof Boolean))
					continue;
				if(((Boolean)obj).booleanValue()){
					setBlockStyle(sheet, style.getStartRow() + rowOffset, style.getEndRow() + rowOffset,
							style.getStartCol(), style.getEndCol(),
							styleMap.get(style.getCellIndex()));
				}
			}
		}
		for(ExcelCell cellDefinition: blockDefinition.getCells()){
			String dataName = cellDefinition.getDataExpr() == null? cellDefinition.getDataName() : cellDefinition.getDataExpr();
			if(dataName.startsWith("="))
				dataName = ExcelUtil.offsetFormula(dataName, rowOffset, 0);
			setCellValue(sheet, cellDefinition.getRow() + rowOffset, cellDefinition.getCol(), 
					dataName, stack);	
			if(styleMap.keySet().size() > 0){
				for(ExcelCellConditionStyle style: cellDefinition.getStyles()){
					Object obj = stack.getValue(style.getCondition());
					if(obj == null || !(obj instanceof Boolean))
						continue;
					if(((Boolean)obj).booleanValue()){
						setCellStyle(sheet, cellDefinition.getRow() + rowOffset, cellDefinition.getCol(),
								styleMap.get(style.getCellIndex()));
					}
				}
			}
		}
		if(blockDefinition.getChildBlock() != null){
			Object colValue = 
				stack.getValue(blockDefinition.getChildBlock().getDataName());
			if(colValue == null) return;
			Collection<? extends Object> listValue;
			if(!(colValue instanceof Collection)){
				if(colValue.getClass().isArray())
					listValue = Arrays.asList(colValue);
				else{
					ArrayList<Object> list = new ArrayList<Object>();
					list.add(colValue);
					listValue = list;
				}
			}else
				listValue = (Collection<? extends Object>)colValue;
			List<CellRangeAddress> childMergedRegions = null;
			if(mergedRegions != null){
				childMergedRegions = new ArrayList<CellRangeAddress>();
				for(CellRangeAddress cra: mergedRegions){
					if(cra.getFirstRow() >= blockDefinition.getChildBlock().getStartRow() &&
							cra.getFirstColumn() >= blockDefinition.getChildBlock().getStartCol() &&
							cra.getLastRow() <= blockDefinition.getChildBlock().getEndRow() &&
							cra.getLastColumn() <= blockDefinition.getChildBlock().getEndCol()){
						childMergedRegions.add(cra);
					}
				}
			}
			int colStep = 0;
			Object preObj = null;
			for(Object obj: listValue){
				stack.push(obj);
				stack.addContext("preColumn", preObj);
				stack.addContext("columnNum", colStep);
				writeCol(sheet, blockDefinition.getChildBlock(), stack, rowOffset, 
						colStep * (blockDefinition.getChildBlock().getEndCol() - blockDefinition.getChildBlock().getStartCol() +1), 
						childMergedRegions, styleMap, writeStatus);
				colStep ++;
				preObj = stack.pop();
			}
			stack.removeContext("preColumn");
			stack.removeContext("columnNum");
		}
	}
	
	private void writeCol(Sheet sheet, ExcelBlock blockDefinition, OgnlStack stack, int rowOffset, int colOffset,
			List<CellRangeAddress> mergedRegions, 
			Map<String,CellStyle> styleMap, WriteStatus writeStatus) throws Exception{
		if(rowOffset > 0 || colOffset > 0){
			ExcelUtil.copyBlock(sheet, blockDefinition.getStartRow(), blockDefinition.getStartCol(), 
				blockDefinition.getEndRow(), blockDefinition.getEndCol(), true, 
				rowOffset, colOffset, mergedRegions);
		}
		if(styleMap.keySet().size() > 0){
			for(ExcelCellConditionStyle style: blockDefinition.getStyles()){
				Object obj = stack.getValue(style.getCondition());
				if(obj == null || !(obj instanceof Boolean))
					continue;
				if(((Boolean)obj).booleanValue()){
					setBlockStyle(sheet, style.getStartRow() + rowOffset, style.getEndRow() + rowOffset,
							style.getStartCol() + colOffset, style.getEndCol() + colOffset,
							styleMap.get(style.getCellIndex()));
				}
			}
		}
		for(ExcelCell cellDefinition: blockDefinition.getCells()){
			String dataName = cellDefinition.getDataExpr() == null? cellDefinition.getDataName() : cellDefinition.getDataExpr();
			if(dataName.startsWith("="))
				dataName = ExcelUtil.offsetFormula(dataName, rowOffset, colOffset);
			setCellValue(sheet, cellDefinition.getRow() + rowOffset, 
					cellDefinition.getCol() + colOffset, 
					dataName, stack);	
			if(styleMap.keySet().size() > 0){
				for(ExcelCellConditionStyle style: cellDefinition.getStyles()){
					Object obj = stack.getValue(style.getCondition());
					if(obj == null || !(obj instanceof Boolean))
						continue;
					if(((Boolean)obj).booleanValue()){
						setCellStyle(sheet, cellDefinition.getRow() + rowOffset, 
								cellDefinition.getCol() + colOffset,
								styleMap.get(style.getCellIndex()));
					}
				}
			}
		}
	}
	
	private void setBlockStyle(Sheet sheet, int startRowIndex, int endRowIndex, 
			int startColIndex, int endColIndex, CellStyle style){
		if(style == null) return;
		for(int rowIndex = startRowIndex; rowIndex <= endRowIndex; rowIndex++){
			Row row = sheet.getRow(rowIndex);
			if(row == null) row = sheet.createRow(rowIndex);
			for(int cellIndex = startColIndex; cellIndex <= endColIndex; cellIndex ++){
				Cell cell = row.getCell(cellIndex);
				if(cell == null) cell = row.createCell(cellIndex);	
				cell.setCellStyle(style);
			}
		}
	}
	
	private void setCellStyle(Sheet sheet, int rowIndex, int cellIndex, CellStyle style){
		if(style == null) return;
		Row row = sheet.getRow(rowIndex);
		if(row == null) row = sheet.createRow(rowIndex);
		Cell cell = row.getCell(cellIndex);
		if(cell == null) cell = row.createCell(cellIndex);			
		if(cell.getCellStyle() == null || (cell.getCellType() != Cell.CELL_TYPE_NUMERIC)
				|| (!DateUtil.isCellDateFormatted(cell))
				|| DateUtil.isADateFormat(style.getDataFormat(), style.getDataFormatString()))
			cell.setCellStyle(style);
		else{			
			CellStyle cstyle = sheet.getWorkbook().createCellStyle();
			cstyle.cloneStyleFrom(style);
			cstyle.setDataFormat(cell.getCellStyle().getDataFormat());
			cell.setCellStyle(cstyle);
		}
	}
	
	private void setCellValue(Sheet sheet, int rowIndex, int cellIndex, String dataName, OgnlStack stack){
		if(dataName.equals("#")) return;
		Row row = sheet.getRow(rowIndex);
		if(row == null) row = sheet.createRow(rowIndex);
		Cell cell = row.getCell(cellIndex);
		if(cell == null) cell = row.createCell(cellIndex);		
		if(dataName.startsWith("=")){
			//formula
			cell.setCellFormula(dataName.substring(1));
		}else{
			//data
			try {
				Object value = stack.getValue(dataName);
				setCellValue(cell, value);
			} catch (Exception e) {
				//do nothing now
			}
		}			
	}
	
	private void setCellValue(Cell cell, Object value){
		if(value == null){
			cell.setCellValue((String)null);
			return;
		}
		if(value instanceof Date) {
			//if current cell do not formatted as date, set is as one date			
			if(cell.getCellType() != Cell.CELL_TYPE_NUMERIC)
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			if(!DateUtil.isCellDateFormatted(cell)){
				CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
				if(cell.getCellStyle() == null){
					style.cloneStyleFrom(cell.getCellStyle());
				}
				style.setDataFormat(
						cell.getSheet().getWorkbook().getCreationHelper().createDataFormat()
							.getFormat(LoxiaSupportSettings.getInstance().
									get(LoxiaSupportConstants.DATE_PATTERN))
						);
				cell.setCellStyle(style);
			}					
		}
		if(cell.getCellType() == Cell.CELL_TYPE_BLANK ||
				cell.getCellType() == Cell.CELL_TYPE_ERROR ||
				cell.getCellType() == Cell.CELL_TYPE_FORMULA){
			//set cell value without data type transform			
			if(value instanceof Integer)
				cell.setCellValue(new BigDecimal((Integer)value).doubleValue());
			else if(value instanceof Long)
				cell.setCellValue(new BigDecimal((Long)value).doubleValue());
			else if(value instanceof Double)
				cell.setCellValue((Double)value);
			else if(value instanceof BigDecimal)
				cell.setCellValue(((BigDecimal)value).doubleValue());
			else if(value instanceof Date)
				cell.setCellValue((Date)value);
			else
				cell.setCellValue(value.toString());
		}else if(cell.getCellType() == Cell.CELL_TYPE_BOOLEAN){
			if(value instanceof Boolean){
				cell.setCellValue((Boolean)value);
			}
		}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
			if(value instanceof Date){
				cell.setCellValue((Date)value);
			}else{
				if(value instanceof Integer)
					cell.setCellValue(new BigDecimal((Integer)value).doubleValue());
				else if(value instanceof Long)
					cell.setCellValue(new BigDecimal((Long)value).doubleValue());
				else if(value instanceof Double)
					cell.setCellValue((Double)value);
				else if(value instanceof BigDecimal)
					cell.setCellValue(((BigDecimal)value).doubleValue());
				else{
					try {
						cell.setCellValue(Double.parseDouble(value.toString()));
					} catch (NumberFormatException e) {
						//do nothing
					}
				}
			}
		}else{
			cell.setCellValue(value.toString());
		}
	}
	
	public ExcelManipulatorDefinition getDefinition() {
		return definition;
	}
	public void setDefinition(ExcelManipulatorDefinition definition) {
		this.definition = definition;
	}
}
