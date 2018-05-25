package MySpringBoot.parentDemo.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;


public class CommTools {
	
	private static Logger log	= LoggerFactory.getLogger( CommTools.class );
	
	public static Random random = new Random();
	/**
	 * @Title: isNotNull
	 * @author 刘桂炽
	 * @date 2018年1月5日 下午4:01:18
	 * @Description: 非空判断，true 不为空，false 为空
	 * @param obj
	 * @return
	 * @return: boolean
	 */
	public static boolean isNotNull(Object obj){
		if(null!=obj&&!"".equals( obj )){
			return true;
		}
		return false;
	}
	/**
	 * @Title: isNull
	 * @author 刘桂炽
	 * @date 2018年1月15日 下午2:15:57
	 * @Description: 空判断，true 为空，false 不为空
	 * @param obj
	 * @return
	 * @return: boolean
	 */
	public static boolean isNull(Object obj) {
		if(null==obj||"".equals( obj )){
			return true;
		}
		return false;
	}
	/**
	 * @Title: strSubtract
	 * @author 刘桂炽
	 * @date 2018年1月8日 下午1:39:55
	 * @Description: 两个字符转bigDecimal相减
	 * @param str1
	 * @param str2
	 * @return
	 * @return: BigDecimal
	 */
	public static BigDecimal strSubtract(String str1,String str2){
		BigDecimal decimal1 = new BigDecimal( str1 );
		BigDecimal decimal2 = new BigDecimal( str2 );
		return decimal1.subtract( decimal2 );
	}
	/**
	 * @Title: strSubtractInt
	 * @author 刘桂炽
	 * @date 2018年1月16日 上午10:07:23
	 * @Description: 两个字符转double相减 str1-str2
	 * @param str1
	 * @param str2
	 * @return
	 * @return: int
	 */
	public static double strSubtractDouble(String str1,String str2){
		double d1 = Double.parseDouble( str1 );
		double d2 = Double.parseDouble( str2 );
		return d1-d2;
	}
	
	/**
	 * @Title: getResource
	 * @author 刘桂炽
	 * @date 2018年1月10日 下午6:13:04
	 * @Description: 获取配置文件，路径优先级： 同jar包的config文件夹》同jar包的路径》classes路径
	 * @param fileName 文件名称（包括后缀名）
	 * @return
	 * @return: Resource
	 */
	public static Resource getResource(String fileName){
		File file = new File("config",fileName); // 同级目录config下面找
		if (!file.exists()) { // 如果没有，则去从jar同级目录加载
			file = new File(fileName);
		}
		Resource resource = new FileSystemResource( file );
		if (!resource.exists()) { // config目录下还是找不到，那就直接用classpath下的
			resource =new ClassPathResource( fileName);
		}
		return resource;
	}
	
	public static File getFileResource(String fileName){
		File file = new File("config",fileName); // 同级目录config下面找
		if (!file.exists()) { // 如果没有，则去从jar同级目录加载
		    file = new File(fileName);
		}
		Resource resource = new FileSystemResource( file );
		if (!resource.exists()) { // config目录下还是找不到，那就直接用classpath下的
			resource =new ClassPathResource( fileName);
		}
		try {
			return resource.getFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}
	
	public static boolean compareStr(String str1,String str2){
		if(null!=str1){
			return str1.equals( str2 );
		}else{
			return str1==str2;
		}
	}
	
	public static String urlFormate(String url, Map<String, String> data){
		StringBuffer strBuf = new StringBuffer();
		if(CommTools.isNull( data )){
			data = new LinkedHashMap<>();
		}
		String[] strSp = url.split("[?]");
		strBuf.append(strSp[0]);
		if(strSp.length > 2){
			log.error("传入的url格式不正确    "+ url );
			throw new RuntimeException("传入的url格式不正确    "+ url);
		}
		if(strSp.length == 2){
			String[] dataStrList = strSp[1].split("[*]");
			for (String string : dataStrList) {
				String[] info = string.split("=");
				if(CommTools.isNull( data.get( info[0] ) )&&info.length>1){
					data.put(info[0], info[1]);
				}
			}
		}
		Iterator<Entry<String, String>> iterator = data.entrySet().iterator();
		for (boolean first=true; iterator.hasNext(); first=false) {
			Entry<String, String> entry = iterator.next();
			if(first){
				strBuf.append("?"+ entry.getKey()+"="+entry.getValue() );
			}else{
				strBuf.append("&"+ entry.getKey()+"="+entry.getValue() );
			}
		}
		
		return strBuf.toString();
		
	}
	/**
	 * 判断bean属性 是否有空
	 * @param bean
	 * @return
	 */
	public static boolean checkFieldValueNull(Object bean,String ...ignoreProperties) {
		List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);
        boolean result = false;
        if (bean == null) {
            return true;
        }
        Class<?> cls = bean.getClass();
        Method[] methods = cls.getDeclaredMethods();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            try {
                String fieldGetName = parGetName(field.getName());
                if (!checkGetMet(methods, fieldGetName)) {
                    continue;
                }
                Method fieldGetMet = cls.getMethod(fieldGetName, new Class[]{});
                Object fieldVal = fieldGetMet.invoke(bean, new Object[]{});
                if ((fieldVal == null || "".equals(fieldVal) ) && !ignoreList.contains(field.getName())) {
                	result = true;
                	continue;
                }
            } catch (Exception e) {
                continue;
            }
        }
        return result;
    }
	
	/**
	 * 判断bean属性 是否有空
	 * @param bean
	 * @return
	 */
	public static boolean checkFieldValueNull(Object bean) {
        boolean result = false;
        if (bean == null) {
            return true;
        }
        Class<?> cls = bean.getClass();
        Method[] methods = cls.getDeclaredMethods();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            try {
                String fieldGetName = parGetName(field.getName());
                if (!checkGetMet(methods, fieldGetName)) {
                    continue;
                }
                Method fieldGetMet = cls.getMethod(fieldGetName, new Class[]{});
                Object fieldVal = fieldGetMet.invoke(bean, new Object[]{});
                if (fieldVal == null || "".equals(fieldVal)) {
                	result = true;
                	continue;
                    /*if ("".equals(fieldVal)) {
                        result = true;
                    } else {
                        result = false;
                    }*/
                }
            } catch (Exception e) {
                continue;
            }
        }
        return result;
    }
	
	/**
     * 拼接某属性的 get方法
     *
     * @param fieldName
     * @return String
     */
    public static String parGetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_')
            startIndex = 1;
        return "get"
                + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }
    
	/**
     * 拼接某属性的 set方法
     *
     * @param fieldName
     * @return String
     */
    public static String parSetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_')
            startIndex = 1;
        return "set"
                + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }

    /**
     * 判断是否存在某属性的 get方法
     *
     * @param methods
     * @param fieldGetMet
     * @return boolean
     */
    public static boolean checkGetMet(Method[] methods, String fieldGetMet) {
        for (Method met : methods) {
            if (fieldGetMet.equals(met.getName())) {
                return true;
            }
        }
        return false;
    }
    
	/**
	 * 判断bean 是否為空
	 * @param bean
	 * @return
	 */
	public static boolean checkBeanNull(Object bean) {
        boolean result = true;
        if (bean == null) {
            return true;
        }
        Class<?> cls = bean.getClass();
        Method[] methods = cls.getDeclaredMethods();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            try {
                String fieldGetName = parGetName(field.getName());
                if (!checkGetMet(methods, fieldGetName)) {
                    continue;
                }
                Method fieldGetMet = cls.getMethod(fieldGetName, new Class[]{});
                Object fieldVal = fieldGetMet.invoke(bean, new Object[]{});
                if (fieldVal != null && !"".equals(fieldVal)) {
                	result = false;
                	break;
                }
            } catch (Exception e) {
                continue;
            }
        }
        return result;
    }
	
	
	/**
	 * 拼接  length 长度个 singleChar
	 * @param singleChar
	 * @param length
	 * @return
	 */
	public static String appendStr(String singleChar,int length){
		if(length == 0){
			return null;
		}
		StringBuffer srbuf = new StringBuffer();
		for(int i = 0; i < length ; i++){
			srbuf.append(singleChar);
		}
		return srbuf.toString();
	}
	
	
	/**
	 * 获取 source 中 为 null 或者 "" 的属性名
	 * @param source
	 * @return
	 */
	public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null || "".equals(srcValue)) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
	
	/**
	 * 将 soure 中 非空 属性值 赋值到  target 中
	 * @param source
	 * @param target
	 */
	public static void copyPropertiesIgnoreNull(Object source,Object target){
		BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
	}
	
	public static void copyPropertiesIgnoreNull(Object source,Object target,String ...ignoreProperties){
		//BeanUtils.copyProperties(source, target, ignoreProperties);
		BeanUtils.copyProperties(source, target, ArrayUtils.addAll(ignoreProperties, getNullPropertyNames(source)));
	}
	
	/**
	 * 统计 str 中 有多少个 charA
	 * @param str
	 * @param charA
	 * @return
	 */
	public static int countChart(String str, String charA){
		// 根据指定的字符构建正则  
        Pattern pattern = Pattern.compile(charA);  
        // 构建字符串和正则的匹配  
        Matcher matcher = pattern.matcher(str);  
        int count = 0;  
        // 循环依次往下匹配  
        while (matcher.find()){ // 如果匹配,则数量+1  
            count++;  
        }  
        return  count;
	}
	
	public static String generUUI(){
		String uuId = UUID.randomUUID().toString().replaceAll("-", "");
		return uuId;
	}
	
    /**
    * 把原始字符串分割成指定长度的字符串列表
    * 
    * @param inputString
    *            原始字符串
    * @param length
    *            指定长度
    * @return
    */
   public static List<String> getStrList(String inputString, int length) {
       int size = inputString.length() / length;
       if (inputString.length() % length != 0) {
           size += 1;
       }
       return getStrList(inputString, length, size);
   }

   /**
    * 把原始字符串分割成指定长度的字符串列表
    * 
    * @param inputString
    *            原始字符串
    * @param length
    *            指定长度
    * @param size
    *            指定列表大小
    * @return
    */
   public static List<String> getStrList(String inputString, int length,
           int size) {
       List<String> list = new ArrayList<String>();
       for (int index = 0; index < size; index++) {
           String childStr = substring(inputString, index * length,
                   (index + 1) * length);
           list.add(childStr);
       }
       return list;
   }

   /**
    * 分割字符串，如果开始位置大于字符串长度，返回空
    * 
    * @param str
    *            原始字符串
    * @param f
    *            开始位置
    * @param t
    *            结束位置
    * @return
    */
   public static String substring(String str, int f, int t) {
       if (f > str.length())
           return null;
       if (t > str.length()) {
           return str.substring(f, str.length());
       } else {
           return str.substring(f, t);
       }
   }
   
   /**
    * 替换指定位置的字符
    * @param str 原字符串
    * @param index  位置
    * @param replaceChar 替换的字符
    * @return
    */
   public static String replaceCharByIndex(String str, int index ,char replaceChar){
	   char[] cs = str.toCharArray();
       cs[index] = replaceChar;
       return new String(cs);
   }
   
   /**
    * 获取指定位置的 字符
    * @param str
    * @param index
    * @return
    */
   public static char getCharByIndex(String str, int index){
	   char[] cs = str.toCharArray();
	   return cs[index];
   }
	
}


