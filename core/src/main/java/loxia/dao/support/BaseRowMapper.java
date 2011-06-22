package loxia.dao.support;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

public abstract class BaseRowMapper<T> implements RowMapper<T> {
	protected static final Logger logger = LoggerFactory.getLogger(BaseRowMapper.class);

	@SuppressWarnings("unchecked")
	protected <K> K getResultFromRs(ResultSet rs, String alias, Class<K> clazz) throws SQLException{
		logger.debug("[RowMapper]--[{}]: {}", alias, clazz);
		if(clazz == null)
			return (K) rs.getObject(alias);
		else{
			K value = null;			
			if(clazz.equals(String.class))
				value = (K) rs.getString(alias);
			else if(clazz.equals(java.math.BigDecimal.class))
				value = (K) rs.getBigDecimal(alias);
			else if(clazz.equals(java.io.InputStream.class))
				value = (K) rs.getBinaryStream(alias);
			else if(clazz.equals(java.sql.Blob.class))
				value = (K) rs.getBlob(alias);
			else if(clazz.equals(Boolean.class))
				value = (K) new Boolean(rs.getBoolean(alias));
			else if(clazz.equals(Byte.class))
				value = (K) new Byte(rs.getByte(alias));
			else if(clazz.equals(Byte[].class))
				value = (K) rs.getBytes(alias);
			else if(clazz.equals(java.io.Reader.class))
				value = (K) rs.getCharacterStream(alias);
			else if(clazz.equals(java.sql.Clob.class))
				value = (K) rs.getClob(alias);
			else if(clazz.equals(java.sql.Date.class))
				value = (K) rs.getDate(alias);
			else if(clazz.equals(Double.class))
				value = (K) new Double(rs.getDouble(alias));
			else if(clazz.equals(Float.class))
				value = (K) new Float(rs.getFloat(alias));
			else if(clazz.equals(Integer.class))
				value = (K) new Integer(rs.getInt(alias));
			else if(clazz.equals(Long.class))
				value = (K) new Long(rs.getLong(alias));
			else if(clazz.equals(Short.class))
				value = (K) new Short(rs.getShort(alias));
			else if(clazz.equals(java.sql.Time.class))
				value = (K) rs.getTime(alias);
			else if(clazz.equals(java.sql.Timestamp.class))
				value = (K) rs.getTimestamp(alias);
			else if(clazz.equals(java.util.Date.class))
				value = (K) (rs.getTimestamp(alias) == null ? null :new java.util.Date(rs.getTimestamp(alias).getTime()));
			else
				value = (K) rs.getObject(alias);
			return (value == null ||rs.wasNull()) ? null : value;
		}
	}

	@SuppressWarnings("unchecked")
	protected <K> K getResultFromRs(ResultSet rs, int icolumn, Class<K> clazz) throws SQLException{
		logger.debug("[RowMapper]--[Column {}]: {}", icolumn, clazz);
		if(clazz == null)
			return (K) rs.getObject(icolumn);
		else{
			K value = null;
			if(clazz.equals(String.class))
				value = (K) rs.getString(icolumn);
			else if(clazz.equals(java.math.BigDecimal.class))
				value = (K) rs.getBigDecimal(icolumn);
			else if(clazz.equals(java.io.InputStream.class))
				value = (K) rs.getBinaryStream(icolumn);
			else if(clazz.equals(java.sql.Blob.class))
				value = (K) rs.getBlob(icolumn);
			else if(clazz.equals(Boolean.class))
				value = (K) new Boolean(rs.getBoolean(icolumn));
			else if(clazz.equals(Byte.class))
				value = (K) new Byte(rs.getByte(icolumn));
			else if(clazz.equals(Byte[].class))
				value = (K) rs.getBytes(icolumn);
			else if(clazz.equals(java.io.Reader.class))
				value = (K) rs.getCharacterStream(icolumn);
			else if(clazz.equals(java.sql.Clob.class))
				value = (K) rs.getClob(icolumn);
			else if(clazz.equals(java.sql.Date.class))
				value = (K) rs.getDate(icolumn);
			else if(clazz.equals(Double.class))
				value = (K) new Double(rs.getDouble(icolumn));
			else if(clazz.equals(Float.class))
				value = (K) new Float(rs.getFloat(icolumn));
			else if(clazz.equals(Integer.class))
				value = (K) new Integer(rs.getInt(icolumn));
			else if(clazz.equals(Long.class))
				value = (K) new Long(rs.getLong(icolumn));
			else if(clazz.equals(Short.class))
				value = (K) new Short(rs.getShort(icolumn));
			else if(clazz.equals(java.sql.Time.class))
				value = (K) rs.getTime(icolumn);
			else if(clazz.equals(java.sql.Timestamp.class))
				value = (K) rs.getTimestamp(icolumn);
			else if(clazz.equals(java.util.Date.class))
				value = (K) (rs.getTimestamp(icolumn) == null ? null :new java.util.Date(rs.getTimestamp(icolumn).getTime()));
			else
				value = (K) rs.getObject(icolumn);
			return (value == null ||rs.wasNull()) ? null : value;
		}
	}
}
