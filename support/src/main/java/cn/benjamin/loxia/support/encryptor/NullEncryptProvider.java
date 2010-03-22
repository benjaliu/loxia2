package cn.benjamin.loxia.support.encryptor;

public class NullEncryptProvider implements EncryptProvider{
    public String getDigest(byte[] originData) throws EncryptException{
        return new String(originData);
    }

	public String getName() {
		return "NULL";
	}
}
