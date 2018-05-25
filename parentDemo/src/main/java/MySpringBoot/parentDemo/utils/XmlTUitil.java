package MySpringBoot.parentDemo.utils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XmlTUitil {

	/**
	 * @Decription TODO 获取DocumentBuilder对象
	 * @date 2016年10月12日 下午8:29:46
	 * @return
	 */
/*	public static DocumentBuilder getDocumentBuilder() {
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
				"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
		// 获得DocumentBuilderFactory对象
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return db;
	}
*/
	/**
	 * @Decription TODO 有些小国家是没其他城市的，所以没有子节点.
	 *             如果有就获取所有一级城市节点：国外有些国家是没有州的state，那么这些国家的一级城市就是city节点的属性值.
	 *             中华人民共和国全部的省份城市都要保存
	 * @date 2016年10月11日 下午2:46:25
	 */
/*	public void domParseTest() {

		try {
			// 获得DocumentBuilder对象
			DocumentBuilder builder = this.getDocumentBuilder();
			// 通过xml文件输入流获取xml的document对象,parse有输入流的重载方法。这里我用的是源文件路经作为参数(懒^_^)
			Document document = builder.parse("LocList.xml");
			// 获取所有国家节点
			NodeList countrys = document.getElementsByTagName("CountryRegion");
			// 要保存到数据库的数据题条目的计数器
			int count = 0;
			// 遍历所有CountryRegion节点
			for (int j = 0; j < countrys.getLength(); j++) {
				String countryName = "";
				String countryCode = "";
				// 获取当前CountryRegion节点
				Element country = (Element) countrys.item(j);
				// 判断是否拥有属性
				if (country.hasAttributes()) {
					// 这是一种获取属性值得方式，当属性较多时可用遍历去获取，下面有遍历获取的的方法
					countryName = country.getAttribute("Name");
					countryCode = country.getAttribute("Code");
					System.out.println("国家：" + countryName + "  代码:" + countryCode);
					count++;
				}
				// 判断当前CountryRegion节点是否拥有子节点
				if (country.hasChildNodes()) {
					// 获取CountryRegion节点的子节点
					NodeList states = country.getChildNodes();
					// 遍历所有CountryRegion节点下的所有State节点
					for (int k = 0; k < states.getLength(); k++) {
						// 缓存city或state节点的name属性值
						String cityName = "";
						// 缓存city或state节点的code属性值
						String cityCode = "";
						// 中国下面的子节点逻辑（if) 其他（else if 和else)
						// 当elseif和前面的if或elseif同时成立时，执行前面的那一个if或elseif后面的elseif不执行。
						if ("State".equals(states.item(k).getNodeName()) && states.item(k).hasAttributes()
								&& "中华人民共和国".equals(countryName)) {
							if (states.item(k).getNodeType() == Node.ELEMENT_NODE) {
								NamedNodeMap stateAttr = states.item(k).getAttributes();
								for (int i = 0; i < stateAttr.getLength(); i++) {
									Attr attr = (Attr) stateAttr.item(i);
									if ("Name".equals(attr.getNodeName())) {
										cityName = attr.getNodeValue();
									} else {
										cityCode = attr.getNodeValue();
									}
								}
								System.out.println(countryName + "的省份： " + cityName + "  代码:" + cityCode);
								count++;
								if (states.item(k).hasChildNodes()) {
									NodeList cityList = states.item(k).getChildNodes();
									// 将省份保留下来
									String curState = cityName;
									for (int city = 0; city < cityList.getLength(); city++) {
										if (cityList.item(city).hasAttributes()) {
											NamedNodeMap cityAttr = cityList.item(city).getAttributes();
											for (int i = 0; i < cityAttr.getLength(); i++) {
												Attr attr = (Attr) cityAttr.item(i);
												if ("Name".equals(attr.getNodeName())) {
													cityName = attr.getNodeValue();
												} else {
													cityCode = attr.getNodeValue();
												}
											}
											System.out.println(countryName + curState + "的城市或地区： " + cityName + "  代码:"
													+ cityCode);
											count++;
										}
									}
								}
							}

						} else if ("State".equals(states.item(k).getNodeName()) && states.item(k).hasAttributes()) {
							NamedNodeMap cityAttr = states.item(k).getAttributes();
							for (int i = 0; i < cityAttr.getLength(); i++) {
								Attr attr = (Attr) cityAttr.item(i);
								if ("Name".equals(attr.getNodeName())) {
									cityName = attr.getNodeValue();
								} else {
									cityCode = attr.getNodeValue();
								}
							}
							System.out.println(countryName + "的一级城市： " + cityName + "  代码:" + cityCode);
							count++;
						} else {
							*//**
							 * 前面是没有属性的state节点时System.out.println(states.item(k)
							 * .getNodeName());输出下列内容: #text TEXT_NODE State
							 * ELEMENT_NODE #text 所以要加一个判断
							 **//*
							if (states.item(k).getNodeType() == Node.ELEMENT_NODE) {
								if (states.item(k).hasChildNodes()) {
									NodeList cityList = states.item(k).getChildNodes();
									for (int city = 0; city < cityList.getLength(); city++) {
										if (cityList.item(city).hasAttributes()) {
											NamedNodeMap cityAttr = cityList.item(city).getAttributes();
											for (int i = 0; i < cityAttr.getLength(); i++) {
												Attr attr = (Attr) cityAttr.item(i);
												if ("Name".equals(attr.getNodeName())) {
													cityName = attr.getNodeValue();
												} else {
													cityCode = attr.getNodeValue();
												}
											}
											System.out.println(countryName + "的一级城市： " + cityName + "  代码:" + cityCode);
											count++;
										}
									}
								} else {
									NamedNodeMap cityAttr = states.item(k).getAttributes();
									for (int i = 0; i < cityAttr.getLength(); i++) {
										Attr attr = (Attr) cityAttr.item(i);
										if ("Name".equals(attr.getNodeName())) {
											cityName = attr.getNodeValue();
										} else {
											cityCode = attr.getNodeValue();
										}
									}
									System.out.println(countryName + "的一级城市： " + cityName + "  代码:" + cityCode);
									count++;
								}

							}
						}
					}
				} else {
					System.out.println(countryName + "是个小国家，没有一级城市！");
				}
			}
			System.out.println("一共有：" + count + "的数据！");
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
*/
	/**
	 * dom生成xml文件
	 */
/*	public void domCreateXml() {
		// 创建DocumentBuilder对象
		DocumentBuilder db = this.getDocumentBuilder();
		// 生成一个Dom树
		Document document = db.newDocument();
		// 去掉standalone="no"声明,说明只是一个简单的xml,没有特殊DTD(document type
		// definition文档类型定义)规范
		document.setXmlStandalone(true);
		// 创建Location根节点
		Element rootElement = document.createElement("Location");
		// 创建CountryRegion节点
		Element country = document.createElement("CountryRegion");
		country.setAttribute("Name", "中国");
		country.setAttribute("Code", "1");
		// 创建State节点
		Element state = document.createElement("State");
		state.setAttribute("Name", "四川");
		state.setAttribute("Code", "22");
		// 创建city节点
		Element city = document.createElement("City");
		city.setAttribute("Name", "成都");
		city.setAttribute("Code", "cd");
		// 将city是state下的子节点，将city加入到state中
		state.appendChild(city);
		// 将state是country下的子节点，将state加入到country中
		country.appendChild(state);
		// 将country是Location下的子节点，将state加入到country中
		rootElement.appendChild(country);

		// 将包含了子节点的rootElement添加到document中
		document.appendChild(rootElement);
		// 实例化工厂类，工厂类不能使用new关键字实例化创建对象
		TransformerFactory transFactory = TransformerFactory.newInstance();
		try {
			// 创建transformer对象
			Transformer transformer = transFactory.newTransformer();
			// 设置换行
			transformer.setOutputProperty(OutputKeys.INDENT, "Yes");
			// 构造转换,参数都是抽象类，要用的却是更具体的一些类，这些的类的命名有一些规律的。
			transformer.transform(new DOMSource(document), new StreamResult("LocListDom.xml"));
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
*/
	public static Map<String, String> readAnswerXml() {

		try {
			XMLWriter writer = null;// 声明写XML的对象
			SAXReader reader = new SAXReader();

			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GBK");// 设置XML文件的编码格式
			// 获得DocumentBuilder对象
			// DocumentBuilder builder = getDocumentBuilder();
			// 通过xml文件输入流获取xml的document对象,parse有输入流的重载方法。这里我用的是源文件路经作为参数(懒^_^)
			File file = CommTools.getFileResource("answer.xml");
			if (file.exists()) {
				 Document document = reader.read(file);// 读取XML文件
				 Element root = document.getRootElement();// 得到根节点
				 List<Element> elements = root.elements();
				 for (Element e : elements){
					 System.err.println(e.getName());
					 System.err.println(e.attributeValue("id"));
					 List<Element> values = e.elements();
					 for(Element v : values){
						 System.err.println(v.getStringValue());
					 }
					 
				 }
				 
				
			}
			//Document document = builder.parse(rescoure.getFile());
			// Document document = builder.parse("LocList.xml");
			// 获取所有国家节点
			//NodeList answers = document.getElementsByTagName("answer");

		} catch (Exception e) {
			e.printStackTrace();

		}

		return null;

	}

	public static void main(String[] args) {
		readAnswerXml();
	}

}
