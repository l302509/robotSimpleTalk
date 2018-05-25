package MySpringBoot.parentDemo.domain.constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import MySpringBoot.parentDemo.utils.CommTools;
import MySpringBoot.parentDemo.utils.LevenshteinUtil;

public class AnswersResults {

	public static Map<String, List<String>> ansMap = new ConcurrentHashMap<>();
	
	
	/**
	 * 返回str匹配度最高的 关键字及匹配度  list[0]为匹配度 float list[1]为 关键字key list[2]为回答内容 List<String>
	 * @param str
	 * @return
	 */
	public static List getMaxRateValue(String str){
		List outList = new ArrayList<>();
		outList.add(0, 0.00f);
		outList.add(1,null);
		outList.add(2,null);
		for(Entry<String,List<String>> entry : ansMap.entrySet()){
			float rate = (float) outList.get(0);
			float newRate = LevenshteinUtil.getSimilarityRatio(str, entry.getKey());
			if(newRate > rate){
				outList.set(0, newRate);
				outList.set(1,entry.getKey());
				outList.set(2,entry.getValue());
			}
		}
		return outList;
		
	}
	/**
	 * 添加对应ID下的回答
	 * @param id
	 * @param value
	 */
	public static void addAnswerValue(String id,String value){
		XMLWriter writer = null;// 声明写XML的对象
		SAXReader reader = new SAXReader();
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");// 设置XML文件的编码格式
		File file = CommTools.getFileResource("answer.xml");
		try {
			if (file.exists()) {
				Document document = reader.read(file);// 读取XML文件
				Element targetElement = document.elementByID(id);
				if(null == targetElement){
					Element root = document.getRootElement();// 得到根节点
					targetElement = root.addElement("answer");
					targetElement.addAttribute("ID", id);
				}
				Element valueElement = targetElement.addElement("value");
				valueElement.setText(value);
				writer = new XMLWriter(new FileOutputStream(file), format);
				writer.write(document);
				writer.close();
			}
			
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(null != writer && "".equals(writer)){
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	

	static {
		try {
			XMLWriter writer = null;// 声明写XML的对象
			SAXReader reader = new SAXReader();
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");// 设置XML文件的编码格式
			File file = CommTools.getFileResource("answer.xml");
			if (file.exists()) {
				Document document = reader.read(file);// 读取XML文件
				Element root = document.getRootElement();// 得到根节点
				List<Element> elements = root.elements();
				for (Element e : elements) {
					List<String> answerValues = new ArrayList<>();
					//System.err.println(e.getName());
					System.err.println(e.attributeValue("id"));
					List<Element> values = e.elements();
					for (Element v : values) {
						answerValues.add(v.getStringValue());
						System.err.println(v.getStringValue());
					}
					ansMap.put(e.attributeValue("ID"), answerValues);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
