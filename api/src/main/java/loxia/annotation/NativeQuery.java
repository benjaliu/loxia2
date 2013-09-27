package loxia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import loxia.dao.ColumnTranslator;
import loxia.dao.support.DummyColumnTranslator;

/**
 * NativeQuery<br/>
 * 用于基于SQL的查询，通过相应属性的设置可以连接查询语句和设置查询结果，详细说明如下：<br/>
 * 一个可以标识为NativeQuery的查询方法表现如下：<br/>
 * <code>[Object][List&lt;Object&gt;][Pagination&lt;Object&gt;] methodName([int start, int size],[Sort[] sorts],[Page page],[RowMapper&lt;Object&gt; rowMapper],[... Query Parameters]);</code><br/>
 * 返回值可以是一个对象或者一个对象的列表，如果涉及分页查询，返回对象可以是一个对象的列表或者是一个<code>Pagination</code>对象<br/>
 * 如果涉及分页，可以在参数中增加<code>Page</code>，或者使用<code>int start, int size</code>方案。
 * 区别在于如果使用前者<code>pagable</code>属性不用设置，且参数位置不限定；如果是后者，参数必须是开头两个，且必须设置<code>pagable</code>属性为<code>true</code>。<br/>
 * 如果返回对象类型是一个<code>Entity</code>（模型上有{@link Entity}标注），可以通过设置<code>model</code>属性完成结果集和对象的绑定。
 * 绑定关系利用的是{@link Column}和{@link JoinColumn}中的字段名称定义。<br/>
 * 如果返回对象类型只是一个Pojo，可以通过在Pojo中使用{@link loxia.annotation.Column}标注属性（标注在getter方法上），然后通过设置<code>model</code>属性完成结果集和对象的绑定。<br/>
 * 如果返回对象不满足以上两种情况，则需要RowMapper参数，参数位置不限定。<br/>
 * 如果返回对象希望使用<code>Map&lt;String,Object&gt;</code>或者<code>List&lt;Map&lt;String,Object&gt;&gt;</code>或者<code>Pagniation&lt;Map&lt;String,Object&gt;&gt;</code>，则
 * 可以不用定义RowMapper参数，但必须在标注的<code>alias</code>和<code>classes</code>属性中显式定义需要读取的字段名称和数据类型。<br/>
 * 其他查询参数需用{@link QueryParam}定义，参数可以是一个基础数据类型或者<code>Map&lt;String,Object&gt;</code>。<br/>
 * 如果需要传递排序，请使用<code>Sort[]</code>参数（{@link Sort}的数组），位置不限定。<br/>
 * <br/>
 * 属性说明：<br/>
 * <ul>
 * <li>value：用于指定查询语句的名称，如SalesOrder.findByCode。如果不指定，默认会使用<code>&lt;Entity&gt;.&lt;Method&gt;</code></li>
 * <li>pagable: 用于指定当前查询是否分页。如果查询方法的参数中有{@link Page}类型的参数则不考虑此属性，一定会进行分页</li>
 * <li>withGroupby：在返回{@link Pagination}类型结果的时候使用，其他情况下不考虑。<br/>
 * 由于<code>Pagination</code>需要一次额外的统计数量的查询，因此将会根据现有查询语句构造相应的统计数量的查询语句。
 * 如果当前查询语句结构简单则不需要设置，如果结构复杂，如含group子句或者union时，设置为<code>true</code>。</li>
 * <li>model：用于指定返回值中对象的数据类型</li>
 * <li>translator: 用于指定列名称的翻译对照器，即如何将字段和属性对照起来，不指定时默认<code>Entity</code>会用到{@link JpaEntityColumnTranslator}，
 * 否则用到{@link UpperCaseColumnTranslator}。</li>
 * <li>alias：返回<code>Map&lt;String,Object&gt;</code>类型数据时需指定的列名列表</li>
 * <li>classes：返回<code>Map&lt;String,Object&gt;</code>类型数据时需指定的类列表</li>
 * </ul>
 * @author Benjamin
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NativeQuery {
	String value() default "";
	boolean pagable() default false;
	boolean withGroupby() default false;
	Class<?> model() default DEFAULT.class;
	Class<? extends ColumnTranslator> translator() default DummyColumnTranslator.class; 
	String[] alias() default {};
	Class<?>[] clazzes() default {};
	
	public static final class DEFAULT {}
}
