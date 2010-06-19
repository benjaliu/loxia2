package loxia.utils;

import java.util.HashSet;
import java.util.Set;

public class PropListCopyable implements CopyableInterface {
	private Set<String> props;
	
	public PropListCopyable(String... propList){
		props = new HashSet<String>();
		if(propList != null){
			for(String prop : propList)
				props.add(prop);
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean isCopyable(String propertyName, Object propertyValue, Class clazz) {
		if(props.contains(propertyName))
			return true;
		else
			return false;
	}

}
