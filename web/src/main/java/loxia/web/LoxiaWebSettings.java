package loxia.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loxia.support.LoxiaSupportSettings;
import loxia.utils.ClassLoaderUtils;

public class LoxiaWebSettings {
final static Logger logger = LoggerFactory.getLogger(LoxiaWebSettings.class);
	
	private static LoxiaWebSettings instance;
	private static final String[] CONFIGS = new String[]{"loxiaweb","loxia/web-default"};
	
	private List<Properties> props = new ArrayList<Properties>(); 
	
	private LoxiaWebSettings(){
		for(String config: CONFIGS){
			InputStream is = ClassLoaderUtils.getResourceAsStream(
					config + ".properties", LoxiaSupportSettings.class);
			if(is != null){
				Properties prop = new Properties();
				try {
					prop.load(is);
					props.add(prop);
				} catch (IOException e) {
					e.printStackTrace();			
					logger.warn("Error occurs when loading {}.properties", config);
				}
			}else{
				logger.warn("Could not find {}.properties", config);
			}
		}
	}
	
	public static LoxiaWebSettings getInstance(){
		if(instance == null) instance = new LoxiaWebSettings();		
		return instance;
	}
	
	public String get(String name){
		String result = null;
		for(Properties prop: props){
			result = prop.getProperty(name);
			if(result != null) break;
		}
		return result;
	}
}
