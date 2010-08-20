package loxia.support.excel;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loxia.support.excel.definition.ExcelManipulatorDefinition;
import loxia.support.excel.definition.ExcelSheet;
import loxia.support.excel.impl.DefaultExcelReader;
import loxia.support.excel.impl.DefaultExcelWriter;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ExcelManipulatorFactory {
	static final Logger logger = LoggerFactory.getLogger(ExcelManipulatorFactory.class);
	
	public static final String RULE_FILE = "loxia/support/excel/excelcontent-definition-rule.xml";
	
	private Map<String, ExcelSheet> sheetDefinitions = new HashMap<String, ExcelSheet>();	
	
	@SuppressWarnings("unchecked")
	public void setConfig(String... configurations){
		for(String config: configurations){
			Digester digester = DigesterLoader.createDigester(
					new InputSource(Thread.currentThread().getContextClassLoader()
							.getResourceAsStream(RULE_FILE)));
			digester.setValidating(false);
			try {
				List<ExcelSheet> list =
					(List<ExcelSheet>)digester.parse(Thread.currentThread().getContextClassLoader().getResourceAsStream(config));
				for(ExcelSheet es: list)
					sheetDefinitions.put(es.getName(), es);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Read excel config failed.");
			} catch (SAXException e) {
				e.printStackTrace();
				throw new RuntimeException("Read excel config failed.");
			}
		}
	}
	public ExcelWriter createExcelWriter(Integer styleSheetPosition, String writerClazzName, String... sheets){
		ExcelWriter excelWriter = createExcelWriterInner(writerClazzName, sheets);
		excelWriter.getDefinition().setStyleSheetPosition(styleSheetPosition);
		return excelWriter;
	}
	
	public ExcelWriter createExcelWriter(Integer styleSheetPosition, String... sheets){
		ExcelWriter excelWriter = createExcelWriterInner(null, sheets);
		excelWriter.getDefinition().setStyleSheetPosition(styleSheetPosition);
		return excelWriter;
	}
	
	public ExcelWriter createExcelWriter(String writerClazzName, String... sheets){
		ExcelWriter excelWriter = createExcelWriterInner(writerClazzName, sheets);
		return excelWriter;
	}
	
	public ExcelWriter createExcelWriter(String... sheets){
		return createExcelWriterInner(null, sheets);
	}
	
	private ExcelWriter createExcelWriterInner(String writerClazzName, String... sheets){
		ExcelWriter excelWriter = null;
		if(writerClazzName == null || writerClazzName.trim().length() ==0)
			excelWriter = new DefaultExcelWriter();
		else{
			try {
				Class<?> clazz = loadClass(writerClazzName, this.getClass());
				if(! ExcelWriter.class.isAssignableFrom(clazz))
					throw new IllegalArgumentException(writerClazzName + " is not a valid ExcelWriter");
				excelWriter = (ExcelWriter)clazz.newInstance();
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Class not found:" + writerClazzName);
			} catch (InstantiationException e) {
				throw new RuntimeException("Initiate ExcelWriter[" + writerClazzName + "] failure");
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Initiate ExcelWriter[" + writerClazzName + "] failure");
			}
		}
		ExcelManipulatorDefinition definition = new ExcelManipulatorDefinition();
		for(String sheet: sheets){
			ExcelSheet sheetDefinition = getExcelSheet(sheet);			
			definition.getExcelSheets().add(sheetDefinition);			
		}
		excelWriter.setDefinition(definition);
		return excelWriter;
	}
	
	public ExcelReader createExcelReader(String... sheets){
		return createExcelReader(null, sheets);
	}
	
	public ExcelReader createExcelReader(String readerClazzName, String... sheets){
		ExcelReader excelReader = null;
		if(readerClazzName == null || readerClazzName.trim().length() ==0)
			excelReader = new DefaultExcelReader();
		else{
			try {
				Class<?> clazz = loadClass(readerClazzName, this.getClass());
				if(! ExcelReader.class.isAssignableFrom(clazz))
					throw new IllegalArgumentException(readerClazzName + " is not a valid ExcelReader");
				excelReader = (ExcelReader)clazz.newInstance();
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Class not found:" + readerClazzName);
			} catch (InstantiationException e) {
				throw new RuntimeException("Initiate ExcelReader[" + readerClazzName + "] failure");
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Initiate ExcelReader[" + readerClazzName + "] failure");
			}
		}
		ExcelManipulatorDefinition definition = new ExcelManipulatorDefinition();
		for(String sheet: sheets){
			ExcelSheet sheetDefinition = getExcelSheet(sheet);			
			definition.getExcelSheets().add(sheetDefinition);			
		}
		excelReader.setDefinition(definition);
		return excelReader;
	}
	
	private ExcelSheet getExcelSheet(String sheet){
		ExcelSheet sheetDefinition = sheetDefinitions.get(sheet);
		if(sheetDefinition == null)
			throw new RuntimeException("No sheet defintion found with name: " + sheet);
		return sheetDefinition.cloneSheet();
	}
	
	private Class<?> loadClass(String className, Class<?> callingClass) throws ClassNotFoundException {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException ex) {
                try {
                    return ExcelManipulatorFactory.class.getClassLoader().loadClass(className);
                } catch (ClassNotFoundException exc) {
                    return callingClass.getClassLoader().loadClass(className);
                }
            }
        }
    }
}
