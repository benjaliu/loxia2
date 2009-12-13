package cn.benjamin.loxia.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import cn.benjamin.loxia.utils.ClassLoaderUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoxiaSupportSettings {
final static Logger logger = LoggerFactory.getLogger(LoxiaSupportSettings.class);
	
	private static LoxiaSupportSettings instance;
	private static final String[] CONFIGS = new String[]{"loxiasupport","loxia/support-default"};
	
	private List<Properties> props = new ArrayList<Properties>(); 
	
	private LoxiaSupportSettings(){
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
	
	public static LoxiaSupportSettings getInstance(){
		if(instance == null) instance = new LoxiaSupportSettings();		
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
