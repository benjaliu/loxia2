package loxia.support.encryptor;

public interface EncryptProvider {
    String getDigest(byte[] originData)  throws EncryptException;
    
    String getName();
}
