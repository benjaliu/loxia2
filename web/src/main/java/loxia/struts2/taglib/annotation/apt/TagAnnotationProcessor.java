/*
 * $Id:  $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package loxia.struts2.taglib.annotation.apt;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class TagAnnotationProcessor implements AnnotationProcessor {
	public static final String LOXIA_TAG = "loxia.struts2.taglib.annotation.LoxiaTag";
	public static final String LOXIA_TAG_ATTRIBUTE = "loxia.struts2.taglib.annotation.LoxiaTagAttribute";
    public static final String TAG_ATTRIBUTE = "org.apache.struts2.views.annotations.StrutsTagAttribute";
	public static final String LOXIA_TAG_SKIP_HIERARCHY = "loxia.struts2.taglib.annotation.LoxiaTagSkipInheritance";
	public static final String TAG_SKIP_HIERARCHY = "org.apache.struts2.views.annotations.StrutsTagSkipInheritance";

	private AnnotationProcessorEnvironment environment;
	private AnnotationTypeDeclaration loxiaTagDeclaration;
	private AnnotationTypeDeclaration loxiaTagAttributeDeclaration;
	private AnnotationTypeDeclaration loxiaSkipDeclaration;
	private Map<String, Tag> tags = new TreeMap<String, Tag>();

	public TagAnnotationProcessor(AnnotationProcessorEnvironment env) {
		environment = env;
		loxiaTagDeclaration = (AnnotationTypeDeclaration) environment.getTypeDeclaration(LOXIA_TAG);
		loxiaTagAttributeDeclaration = (AnnotationTypeDeclaration) environment.getTypeDeclaration(LOXIA_TAG_ATTRIBUTE);
		loxiaSkipDeclaration = (AnnotationTypeDeclaration) environment.getTypeDeclaration(LOXIA_TAG_SKIP_HIERARCHY);
	}

	public void process() {
		// make sure all paramters were set
		checkOptions();

		// tags
		Collection<Declaration> loxiaTagDeclarations = environment.getDeclarationsAnnotatedWith(loxiaTagDeclaration);
		Collection<Declaration> loxiaAttributesDeclarations = environment.getDeclarationsAnnotatedWith(loxiaTagAttributeDeclaration);
		Collection<Declaration> loxiaSkipDeclarations = environment.getDeclarationsAnnotatedWith(loxiaSkipDeclaration);

		// find Tags
		for (Declaration declaration : loxiaTagDeclarations) {
			// type
			TypeDeclaration typeDeclaration = (TypeDeclaration) declaration;
			String typeName = typeDeclaration.getQualifiedName();
			Map<String, Object> values = getValues(typeDeclaration, loxiaTagDeclaration);
			// create Tag and apply values found
			Tag tag = new Tag();
			tag.setDescription((String) values.get("description"));
			tag.setName((String) values.get("name"));
			tag.setTldBodyContent((String) values.get("tldBodyContent"));
			tag.setTldTagClass((String) values.get("tldTagClass"));
			tag.setDeclaredType(typeName);
			// tag.setAllowDynamicAttributes((Boolean)
			// values.get("allowDynamicAttributes"));
			// add to map
			tags.put(typeName, tag);
		}

		// find attributes to be skipped
		for (Declaration declaration : loxiaSkipDeclarations) {
			// types will be ignored when hierarchy is scanned
			if (declaration instanceof MethodDeclaration) {
				MethodDeclaration methodDeclaration = (MethodDeclaration) declaration;
				String typeName = methodDeclaration.getDeclaringType().getQualifiedName();
				String methodName = methodDeclaration.getSimpleName();
				String name = String.valueOf(Character.toLowerCase(methodName.charAt(3))) + methodName.substring(4);
				Tag tag = tags.get(typeName);
				if (tag != null) {
					// if it is on an abstract class, there is not tag for it at
					// this point
					tags.get(typeName).addSkipAttribute(name);
				}
			}
		}

		// find Tags Attributes
		for (Declaration declaration : loxiaAttributesDeclarations) {
			// type
			MethodDeclaration methodDeclaration = (MethodDeclaration) declaration;
			String typeName = methodDeclaration.getDeclaringType().getQualifiedName();
			Map<String, Object> values = getValues(methodDeclaration, loxiaTagAttributeDeclaration);
			// create Attribute and apply values found
			TagAttribute attribute = new TagAttribute();
			String name = (String) values.get("name");
			if (name == null || name.length() == 0) {
				// get name from method
				String methodName = methodDeclaration.getSimpleName();
				name = String.valueOf(Character.toLowerCase(methodName.charAt(3))) + methodName.substring(4);
			}
			values.put("name", name);
			populateTagAttributes(attribute, values);
			// add to map
			Tag parentTag = tags.get(typeName);
			if (parentTag != null)
				tags.get(typeName).addTagAttribute(attribute);
			else {
				// an abstract or base class
				parentTag = new Tag();
				parentTag.setDeclaredType(typeName);
				parentTag.setInclude(false);
				parentTag.addTagAttribute(attribute);
				tags.put(typeName, parentTag);
			}
		}

		// we can't process the hierarchy on the first pass because
		// apt does not garantees that the base classes will be processed
		// before their subclasses
		for (Map.Entry<String, Tag> entry : tags.entrySet()) {
			processHierarchy(entry.getValue());
		}

		// save
		saveAsXml();
		saveTemplates();
	}

	private void populateTagAttributes(TagAttribute attribute, Map<String, Object> values) {
		attribute.setRequired((Boolean) values.get("required"));
		attribute.setRtexprvalue((Boolean) values.get("rtexprvalue"));
		attribute.setDefaultValue((String) values.get("defaultValue"));
		attribute.setType((String) values.get("type"));
		attribute.setDescription((String) values.get("description"));
		attribute.setName((String) values.get("name"));
	}

	private void processHierarchy(Tag tag) {
		try {
			Class<?> clazz = Class.forName(tag.getDeclaredType());
			List<String> skipAttributes = tag.getSkipAttributes();
			// skip hierarchy processing if the class is marked with the skip
			// annotation
			while ((getAnnotation(LOXIA_TAG_SKIP_HIERARCHY, clazz.getAnnotations()) == null 
					|| getAnnotation(TAG_SKIP_HIERARCHY, clazz.getAnnotations()) == null )
					&& ((clazz = clazz.getSuperclass()) != null)) {
				Tag parentTag = tags.get(clazz.getName());
				// copy parent annotations to this tag
				if (parentTag != null) {
					for (TagAttribute attribute : parentTag.getAttributes()) {
						if (!skipAttributes.contains(attribute.getName()))
							tag.addTagAttribute(attribute);
					}
				} else {
					// Maybe the parent class is already compiled
					addTagAttributesFromParent(tag, clazz);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void addTagAttributesFromParent(Tag tag, Class<?> clazz) throws ClassNotFoundException {
		try {
			BeanInfo info = Introspector.getBeanInfo(clazz);
			PropertyDescriptor[] props = info.getPropertyDescriptors();
			List<String> skipAttributes = tag.getSkipAttributes();

			// iterate over class fields
			for (int i = 0; i < props.length; ++i) {
				PropertyDescriptor prop = props[i];
				Method writeMethod = prop.getWriteMethod();

				// make sure it is public
				if (writeMethod != null && Modifier.isPublic(writeMethod.getModifiers())) {
					// can't use the genertic getAnnotation 'cause the class it
					// not on this jar
					Annotation annotation = getAnnotation(LOXIA_TAG_ATTRIBUTE, writeMethod.getAnnotations());
					if (annotation != null && !skipAttributes.contains(prop.getName())) {
						Map<String, Object> values = getValues(annotation);
						// create tag
						TagAttribute attribute = new TagAttribute();
						values.put("name", prop.getName());
						populateTagAttributes(attribute, values);
						tag.addTagAttribute(attribute);
					}
					
					Annotation annotation2 = getAnnotation(TAG_ATTRIBUTE, writeMethod.getAnnotations());
					if (annotation2 != null && !skipAttributes.contains(prop.getName())) {
						Map<String, Object> values = getValues(annotation2);
						// create tag
						TagAttribute attribute = new TagAttribute();
						values.put("name", prop.getName());
						populateTagAttributes(attribute, values);
						tag.addTagAttribute(attribute);
					}
				}

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Annotation getAnnotation(String typeName, Annotation[] annotations) {
		for (int i = 0; i < annotations.length; i++) {
			if (annotations[i].annotationType().getName().equals(typeName))
				return annotations[i];
		}
		return null;
	}

	private void checkOptions() {
		if (getOption("tlibVersion") == null)
			throw new IllegalArgumentException("'tlibVersion' is missing");
		if (getOption("jspVersion") == null)
			throw new IllegalArgumentException("'jspVersion' is missing");
		if (getOption("shortName") == null)
			throw new IllegalArgumentException("'shortName' is missing");
		if (getOption("description") == null)
			throw new IllegalArgumentException("'description' is missing");
		if (getOption("displayName") == null)
			throw new IllegalArgumentException("'displayName' is missing");
		if (getOption("uri") == null)
			throw new IllegalArgumentException("'uri' is missing");
		if (getOption("outTemplatesDir") == null)
			throw new IllegalArgumentException("'outTemplatesDir' is missing");
		if (getOption("outFile") == null)
			throw new IllegalArgumentException("'outFile' is missing");
	}

	private void saveTemplates() {
		// freemarker configuration
		Configuration config = new Configuration();
		config.setClassForTemplateLoading(getClass(), "");
		config.setObjectWrapper(new DefaultObjectWrapper());

		try {
			// load template
			Template template = config.getTemplate("tag.ftl");
			String rootDir = (new File(getOption("outTemplatesDir"))).getAbsolutePath();
			File rootFile = new File(rootDir);
			rootFile.mkdirs();
			for (Tag tag : tags.values()) {
				if (tag.isInclude()) {
					// model
					HashMap<String, Tag> root = new HashMap<String, Tag>();
					root.put("tag", tag);

					// save file
					BufferedWriter writer = new BufferedWriter(new FileWriter(new File(rootDir, tag.getName() + ".html")));
					try {
						template.process(root, writer);
					} finally {
						writer.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// oops we cannot throw checked exceptions
			throw new RuntimeException(e);
		}
	}

	private void saveAsXml() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;

		try {
			// create xml document
			builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			document.setXmlVersion("1.0");
			document.createTextNode("<!DOCTYPE taglib PUBLIC \"-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.2//EN\" \"http://java.sun.com/dtd/web-jsptaglibrary_1_2.dtd\">");
			
			// taglib
			Element tagLib = document.createElement("taglib");

			//tagLib.setAttribute("xmlns", "http://java.sun.com/xml/ns/j2ee");
			//tagLib.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			//tagLib.setAttribute("xsi:schemaLocation", "http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-jsptaglibrary_2_0.xsd");
			//tagLib.setAttribute("version", getOption("jspVersion"));
			document.appendChild(tagLib);
			// tag lib attributes
			appendTextNode(document, tagLib, "tlib-version", getOption("tlibVersion"), false);
			appendTextNode(document, tagLib, "jsp-version", getOption("jspVersion"), false);
			appendTextNode(document, tagLib, "short-name", getOption("shortName"), false);
			appendTextNode(document, tagLib, "uri", getOption("uri"), false);
			appendTextNode(document, tagLib, "display-name", getOption("displayName"), false);
			appendTextNode(document, tagLib, "description", getOption("description"), true);

			// create tags
			for (Map.Entry<String, Tag> entry : tags.entrySet()) {
				Tag tag = entry.getValue();
				if (tag.isInclude())
					createElement(document, tagLib, tag);
			}

			// save to file
			TransformerFactory tf = TransformerFactory.newInstance();
			tf.setAttribute("indent-number", 2);
			Transformer transformer = tf.newTransformer();
			// if tiger would just format it :(
			// formatting bug in tiger
			// (http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6296446)

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			Source source = new DOMSource(document);
			Result result = new StreamResult(new OutputStreamWriter(new FileOutputStream(getOption("outFile"))));
			transformer.transform(source, result);
		} catch (Exception e) {
			// oops we cannot throw checked exceptions
			throw new RuntimeException(e);
		}
	}

	private String getOption(String name) {
		// there is a bug in the 1.5 apt implementation:
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6258929
		// this is a hack-around
		if (environment.getOptions().containsKey(name))
			return environment.getOptions().get(name);

		for (Map.Entry<String, String> entry : environment.getOptions().entrySet()) {
			String key = entry.getKey();
			String[] splitted = key.split("=");
			if (splitted[0].equals("-A" + name))
				return splitted[1];
		}
		return null;
	}

	private void createElement(Document doc, Element tagLibElement, Tag tag) {
		Element tagElement = doc.createElement("tag");
		tagLibElement.appendChild(tagElement);
		appendTextNode(doc, tagElement, "name", tag.getName(), false);
		appendTextNode(doc, tagElement, "tag-class", tag.getTldTagClass(), false);
		appendTextNode(doc, tagElement, "body-content", tag.getTldBodyContent(), false);
		appendTextNode(doc, tagElement, "description", tag.getDescription(), true);

		// save attributes
		for (TagAttribute attribute : tag.getAttributes()) {
			createElement(doc, tagElement, attribute);
		}

		//appendTextNode(doc, tagElement, "dynamic-attributes", String.valueOf(tag.isAllowDynamicAttributes()), false);
	}

	private void createElement(Document doc, Element tagElement, TagAttribute attribute) {
		Element attributeElement = doc.createElement("attribute");
		tagElement.appendChild(attributeElement);
		appendTextNode(doc, attributeElement, "name", attribute.getName(), false);
		appendTextNode(doc, attributeElement, "required", String.valueOf(attribute.isRequired()), false);
		appendTextNode(doc, attributeElement, "rtexprvalue", String.valueOf(attribute.isRtexprvalue()), false);
		appendTextNode(doc, attributeElement, "description", attribute.getDescription(), true);
	}

	private void appendTextNode(Document doc, Element element, String name, String text, boolean cdata) {
		Text textNode = cdata ? doc.createCDATASection(text) : doc.createTextNode(text);
		Element newElement = doc.createElement(name);
		newElement.appendChild(textNode);
		element.appendChild(newElement);
	}

	/**
	 * Get values of annotation
	 * 
	 * @param declaration
	 *            The annotation declaration
	 * @param type
	 *            The type of the annotation
	 * @return name->value map of annotation values
	 */
	private Map<String, Object> getValues(Declaration declaration, AnnotationTypeDeclaration type) {
		Map<String, Object> values = new TreeMap<String, Object>();
		Collection<AnnotationMirror> annotations = declaration.getAnnotationMirrors();
		// iterate over the mirrors.

		for (AnnotationMirror mirror : annotations) {
			// if the mirror in this iteration is for our note declaration...
			if (mirror.getAnnotationType().getDeclaration().equals(type)) {
				for (AnnotationTypeElementDeclaration annotationType : mirror.getElementValues().keySet()) {
					Object value = mirror.getElementValues().get(annotationType).getValue();
					Object defaultValue = annotationType.getDefaultValue();
					values.put(annotationType.getSimpleName(), value != null ? value : defaultValue);
				}
			}
		}

		// find default values...painful
		for (AnnotationTypeElementDeclaration annotationType : type.getMethods()) {
			AnnotationValue value = annotationType.getDefaultValue();
			if (value != null) {
				String name = annotationType.getSimpleName();
				if (!values.containsKey(name))
					values.put(name, value.getValue());
			}
		}

		return values;
	}

	/**
	 * Get values of annotation
	 * 
	 * @param annotation
	 *            The annotation
	 * @return name->value map of annotation values
	 * @throws IntrospectionException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private Map<String, Object> getValues(Annotation annotation) throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Map<String, Object> values = new TreeMap<String, Object>();
		// if the tag classes were on this project we could just cast to the
		// right type
		// but they are needed on core
		Class<?> annotationType = annotation.annotationType();

		Method[] methods = annotationType.getMethods();
		// iterate over class fields
		for (int i = 0; i < methods.length; ++i) {
			Method method = methods[i];
			if (method != null && method.getParameterTypes().length == 0) {
				Object value = method.invoke(annotation, new Object[0]);
				values.put(method.getName(), value);
			}
		}

		return values;
	}
}
