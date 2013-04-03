package loxia.sample;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import loxia.support.excel.ExcelKit;
import loxia.support.excel.ExcelWriter;
import loxia.support.excel.WriteStatus;
import loxia.utils.ChineseCalendarGB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration(locations="classpath*:spring.xml")
public class ExcelWriterTest extends AbstractTestNGSpringContextTests {

	@Resource
	private ApplicationContext ac;
	
	@Autowired
	@Qualifier("report1Writer")
	private ExcelWriter report1ExcelWriter;
	
	@Autowired
	@Qualifier("report2Writer")
	private ExcelWriter report2ExcelWriter;
	
	@Autowired
	@Qualifier("report3Writer")
	private ExcelWriter report3ExcelWriter;
	
	@Autowired
	@Qualifier("report4Writer")
	private ExcelWriter report4ExcelWriter;
	
	@Autowired
	@Qualifier("report5Writer")
	private ExcelWriter report5ExcelWriter;
	
	@Autowired
	@Qualifier("calendarWriter")
	private ExcelWriter calendarWriter;
	
	@Test
	public void test1() throws Exception{
		Map<String, Object> beans = new HashMap<String, Object>();
		Map<String, Object> headMap = new HashMap<String, Object>();
		headMap.put("reportDate", new Date());
		headMap.put("reporter", "汇报者");
		beans.put("head", headMap);
		List<Map<String,Object>> bodyList = new ArrayList<Map<String,Object>>();
		for(int i=0; i< 10; i++){
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("sn", i+1);
			map.put("name", "名称" + i);
			map.put("lineDate", new Date());
			map.put("intValue", i*2+1);
			map.put("floatValue", (i*7+3)/3.0);
			bodyList.add(map);
		}
		beans.put("bodylist", bodyList);
		System.out.println(report1ExcelWriter.getDefinition());
		WriteStatus ws = report1ExcelWriter.write(new FileOutputStream(new File("D:/test.xlsx")), beans);
		for(String str: ExcelKit.getInstance().getWriteStatusMessages(ws, Locale.CHINESE))
			System.out.println(str);
		System.out.println("Done!");
	}
	
	@Test
	public void test2() throws Exception{
		Map<String, Object> beans = new HashMap<String, Object>();
		Map<String, Object> headMap = new HashMap<String, Object>();
		headMap.put("reportDate", new Date());
		headMap.put("reporter", "汇报者");
		beans.put("head", headMap);
		List<Map<String,Object>> bodyList = new ArrayList<Map<String,Object>>();
		for(int i=0; i< 10; i++){
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("sn", i+1);
			map.put("name", "名称" + i);
			map.put("lineDate", new Date());
			map.put("intValue", i*2+1);
			map.put("floatValue", (i*7+3)/3.0);
			bodyList.add(map);
		}
		beans.put("bodylist", bodyList);
		System.out.println(report2ExcelWriter.getDefinition());
		WriteStatus ws = report2ExcelWriter.write(new FileOutputStream(new File("D:/test.xlsx")), beans);
		for(String str: ExcelKit.getInstance().getWriteStatusMessages(ws, Locale.CHINESE))
			System.out.println(str);
		System.out.println("Done!");
	}
	
	@Test
	public void test3() throws Exception{
		Map<String, Object> beans = new HashMap<String, Object>();
		Map<String, Object> headMap = new HashMap<String, Object>();
		headMap.put("reportDate", new Date());
		headMap.put("reporter", "汇报者");
		beans.put("head", headMap);
		List<Map<String,Object>> bodyList = new ArrayList<Map<String,Object>>();
		for(int i=0; i< 10; i++){
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("sn", i+1);
			map.put("name", "名称" + i);
			map.put("lineDate", new Date());
			map.put("intValue", i*2+1);
			map.put("floatValue", (i*7+3)/3.0);
			bodyList.add(map);
		}
		beans.put("bodylist", bodyList);
		System.out.println(report3ExcelWriter.getDefinition());
		WriteStatus ws = report3ExcelWriter.write(new FileOutputStream(new File("D:/test.xlsx")), beans);
		for(String str: ExcelKit.getInstance().getWriteStatusMessages(ws, Locale.CHINESE))
			System.out.println(str);
		System.out.println("Done!");
	}
	
	@Test
	public void test4() throws Exception{
		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("reportDate", new Date());
		beans.put("reporter", "汇报者");
		List<Map<String,Object>> months = new ArrayList<Map<String,Object>>();
		for(int i=1; i<7; i++){
			Map<String, Object> month = new HashMap<String, Object>();
			month.put("month", "" + i + "月");
			months.add(month);
		}
		beans.put("months", months);
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for(int i=0; i< 10; i++){
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("skuId", UUID.randomUUID());
			data.put("skuName", "商品" + i);
			List<Map<String,Object>> monthList = new ArrayList<Map<String,Object>>();
			for(int j=1; j<7; j++){
				Map<String, Object> monthData = new HashMap<String, Object>();
				monthData.put("selling", j*10);
				monthData.put("returns", j*3-1);
				monthData.put("revenue", (j*7-1)*9.9);
				monthList.add(monthData);
			}
			data.put("monthList", monthList);
			list.add(data);
		}
		beans.put("list", list);
		WriteStatus ws = report4ExcelWriter.write(new FileOutputStream(new File("D:/test.xlsx")), beans);
		for(String str: ExcelKit.getInstance().getWriteStatusMessages(ws, Locale.CHINESE))
			System.out.println(str);
		System.out.println("Done!");
	}
	
	@Test
	public void test5() throws Exception{
		Map<String, Object> beans = new HashMap<String, Object>();
		for(int i=1; i<=14; i++){
			beans.put("v"+i, new BigDecimal(Math.random()*60).setScale(2,BigDecimal.ROUND_HALF_UP));
		}
		WriteStatus ws = report5ExcelWriter.write(new FileOutputStream(new File("D:/test.xlsx")), beans);
		for(String str: ExcelKit.getInstance().getWriteStatusMessages(ws, Locale.CHINESE))
			System.out.println(str);
		System.out.println("Done!");
	}
	
	@Test
	public void testGenerateCalendar() throws Exception{
		List<Map<String, Object>> listBeans = new ArrayList<Map<String,Object>>();		
		ChineseCalendarGB cc = new ChineseCalendarGB();
		cc.setGregorian(2013, 1, 1);
		cc.computeChineseFields();
		cc.computeSolarTerms();
		Calendar c = cc.getGregorianCalendar();
		Map<String, Object> bean = null;
		Map<String, Object> oneweek = null;
		while(c.get(Calendar.YEAR) < 2014){
			if(c.get(Calendar.DAY_OF_MONTH) == 1){
				bean = new HashMap<String, Object>();
				bean.put("year", c.get(Calendar.YEAR));
				bean.put("month", c.get(Calendar.MONTH)+1);
				bean.put("sheetName", "" + (c.get(Calendar.MONTH)+1) + "月");
				List<Map<String,Object>> weeks = new ArrayList<Map<String,Object>>();
				bean.put("weeks", weeks);	
				listBeans.add(bean);
			}
			if(c.get(Calendar.DAY_OF_MONTH) == 1 || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
				oneweek = new HashMap<String, Object>();
				List<Map<String,Object>> weeks = (List<Map<String,Object>>)bean.get("weeks");
				weeks.add(oneweek);
			}
			Map<String,Object> day = new HashMap<String, Object>();
			day.put("date", c.get(Calendar.DAY_OF_MONTH));
			day.put("cdate", cc.getShortChineseDayNameWithMonth());
			day.put("terms", cc.getSolarTerms());
			oneweek.put("d" + c.get(Calendar.DAY_OF_WEEK), day);
			cc.rollUpOneDay();
			c.add(Calendar.DATE, 1);
		}
		WriteStatus ws = calendarWriter.writePerSheet(new FileOutputStream(new File("D:/calendar.xlsx")), listBeans);
		for(String str: ExcelKit.getInstance().getWriteStatusMessages(ws, Locale.CHINESE))
			System.out.println(str);
		System.out.println("Done!");
	}
}
