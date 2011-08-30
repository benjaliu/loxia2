package loxia.dao;

public class ReadWriteStatusHolder {

	private static final ThreadLocal<String> readWriteStatusHolder = new ThreadLocal<String>();
	
	public static void setReadWriteStatus(String status){
		readWriteStatusHolder.set(status);
	}
	
	public static String getReadWriteStatus(){
		return readWriteStatusHolder.get();
	}
	
	public static void clearReadWriteStatus(){
		readWriteStatusHolder.remove();
	}
}
