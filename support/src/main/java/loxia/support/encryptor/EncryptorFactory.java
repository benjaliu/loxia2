package loxia.support.encryptor;

import java.util.HashMap;
import java.util.Map;

public class EncryptorFactory {

    private Map<String,EncryptProvider> encryptorProviderMap = new HashMap<String, EncryptProvider>();

    private static EncryptorFactory instance;
    
    private String defaultEncryptor;

	private EncryptorFactory(){
    	registerProvider(new NullEncryptProvider());
    	registerProvider(new MD5EncryptProvider());
    	defaultEncryptor = "NULL";
    }
    
    public static EncryptorFactory getInstance(){
    	if(instance == null)
    		instance = new EncryptorFactory();
    	return instance;
    }
    
    public EncryptProvider getEncryptProvider(){
    	return encryptorProviderMap.get(defaultEncryptor);
    }
    
    public EncryptProvider getEncryptProvider(String name){
    	return encryptorProviderMap.get(name);
    }
    
    public void registerProvider(EncryptProvider provider){
    	encryptorProviderMap.put(provider.getName(), provider);
    }
    
    public String getDefaultEncryptor() {
		return defaultEncryptor;
	}

	public void setDefaultEncryptor(String defaultEncryptor) {
		this.defaultEncryptor = defaultEncryptor;
	}
}
