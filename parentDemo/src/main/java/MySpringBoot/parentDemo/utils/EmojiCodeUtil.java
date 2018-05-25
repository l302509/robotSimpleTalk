package MySpringBoot.parentDemo.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmojiCodeUtil {

	private static Logger LOG = LoggerFactory.getLogger(EmojiCodeUtil.class);

	/**
	 * @Description 将字符串中的emoji表情转换成可以在utf-8字符集数据库中保存的格式（表情占4个字节，需要utf8mb4字符集）
	 * @param str
	 *            待转换字符串
	 * @return 转换后字符串
	 * @throws UnsupportedEncodingException
	 *             exception
	 */
	public static String emojiEncode(String str) throws UnsupportedEncodingException {
		String patternString = "([\\x{10000}-\\x{10ffff}\ud800-\udfff])";

		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			try {
				matcher.appendReplacement(sb, "[[" + URLEncoder.encode(matcher.group(1), "UTF-8") + "]]");
			} catch (UnsupportedEncodingException e) {
				LOG.error("emojiConvert error", e);
				throw e;
			}
		}
		matcher.appendTail(sb);
		LOG.debug("emojiConvert " + str + " to " + sb.toString() + ", len：" + sb.length());
		return sb.toString();
	}

	/**
	 * @Description 将字符串中的emoji表情转换成可以在utf-8字符集数据库中保存的格式（表情占4个字节，需要utf8mb4字符集）
	 * @param str
	 *            待转换字符串
	 * @return 转换后字符串
	 * @throws UnsupportedEncodingException
	 *             exception
	 */
	public static Object emojiEncodeObject(Object bean) {
		try {
			return emojiEncodeObjectFirst(bean);
		} catch (UnsupportedEncodingException e) {
			return bean;
		}
	}

	/**
	 * @Description 还原utf8数据库中保存的含转换后emoji表情的字符串
	 * @param str
	 *            待转换字符串
	 * @return 转换后字符串
	 * @throws UnsupportedEncodingException
	 *             exception
	 */
	public static Object emojiDecodeObject(Object bean) {
		try {
			return emojiDecodeObjectFirst(bean);
		} catch (UnsupportedEncodingException e) {
			return bean;
		}
	}

	private static Object emojiDecodeObjectFirst(Object bean) throws UnsupportedEncodingException {
		if (bean == null) {
			return null;
		}
		if (bean instanceof List) {
			emojiDecodeList(bean);
		} else {
			Class<?> cls = bean.getClass();
			Method[] methods = cls.getDeclaredMethods();
			Field[] fields = cls.getDeclaredFields();
			for (Field field : fields) {
				try {
					String fieldGetName = CommTools.parGetName(field.getName());
					String fieldSetName = CommTools.parSetName(field.getName());
					if (!CommTools.checkGetMet(methods, fieldGetName)) {
						continue;
					}
					if (!CommTools.checkGetMet(methods, fieldSetName)) {
						continue;
					}
					Method fieldGetMet = cls.getMethod(fieldGetName, new Class[] {});
					Method fieldSetMet = cls.getMethod(fieldSetName, new Class[] { field.getType() });
					Object fieldVal = fieldGetMet.invoke(bean, new Object[] {});
					if (fieldVal != null && !"".equals(fieldVal) && String.class == field.getType()) {
						fieldVal = emojiDecode((String) fieldVal);
						fieldSetMet.invoke(bean, new Object[] { fieldVal });
					} else if (List.class == field.getType()) {
						emojiDecodeList(fieldVal);
					}
				} catch (Exception e) {
					continue;
				}
			}
		}
		return bean;
	}

	private static Object emojiDecodeList(Object bean) throws UnsupportedEncodingException {
		if (bean == null) {
			return null;
		}
		List list = (List) bean;
		for (Object b : list) {
			emojiDecodeObjectFirst(b);
		}
		return bean;

	}

	private static Object emojiEncodeObjectFirst(Object bean) throws UnsupportedEncodingException {
		if (bean == null) {
			return null;
		}
		if (bean instanceof List) {
			emojiDecodeList(bean);
		} else {
			Class<?> cls = bean.getClass();
			Method[] methods = cls.getDeclaredMethods();
			Field[] fields = cls.getDeclaredFields();
			for (Field field : fields) {
				try {
					String fieldGetName = CommTools.parGetName(field.getName());
					String fieldSetName = CommTools.parSetName(field.getName());
					if (!CommTools.checkGetMet(methods, fieldGetName)) {
						continue;
					}
					if (!CommTools.checkGetMet(methods, fieldSetName)) {
						continue;
					}
					Method fieldGetMet = cls.getMethod(fieldGetName, new Class[] {});
					Method fieldSetMet = cls.getMethod(fieldSetName, new Class[] { field.getType() });
					Object fieldVal = fieldGetMet.invoke(bean, new Object[] {});
					if (fieldVal != null && !"".equals(fieldVal) && String.class == field.getType()) {
						fieldVal = emojiEncode((String) fieldVal);
						fieldSetMet.invoke(bean, new Object[] { fieldVal });
					} else if (List.class == field.getType()) {
						emojiEncodeList(fieldVal);
					}
				} catch (Exception e) {
					continue;
				}
			}
		}
		return bean;
	}

	private static Object emojiEncodeList(Object bean) throws UnsupportedEncodingException {
		if (bean == null) {
			return null;
		}
		List list = (List) bean;
		for (Object b : list) {
			emojiDecodeObjectFirst(b);
		}
		return bean;

	}

	/**
	 * @Description 还原utf8数据库中保存的含转换后emoji表情的字符串
	 * @param str
	 *            转换后的字符串
	 * @return 转换前的字符串
	 * @throws UnsupportedEncodingException
	 *             exception
	 */
	public static String emojiDecode(String str) throws UnsupportedEncodingException {
		String patternString = "\\[\\[(.*?)\\]\\]";

		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(str);

		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			try {
				matcher.appendReplacement(sb, URLDecoder.decode(matcher.group(1), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LOG.error("emojiRecovery error", e);
				throw e;
			}
		}
		matcher.appendTail(sb);
		LOG.debug("emojiRecovery " + str + " to " + sb.toString());
		return sb.toString();
	}

	public static String filterEmoji(String str) {
		String pattern = "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]";
		String reStr = "";
		Pattern emoji = Pattern.compile(pattern);
		Matcher emojiMatcher = emoji.matcher(str);
		str = emojiMatcher.replaceAll(reStr);
		return str;
	}

	/**
	 * @Description 将字符串中的emoji表情提取出来
	 * @param str
	 *            待转换字符串
	 * @return 转换后字符串
	 * @throws UnsupportedEncodingException
	 *             exception
	 */
	public static String emojiExtract(String str) {
		String patternString = "([\\x{10000}-\\x{10ffff}\ud800-\udfff])";

		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			/*
			 * try { matcher.appendReplacement(sb, "[[" +
			 * URLEncoder.encode(matcher.group(1), "UTF-8") + "]]"); } catch
			 * (UnsupportedEncodingException e) { LOG.error("emojiConvert error"
			 * , e); throw e; }
			 */
			sb.append(matcher.group(1));
		}
		// matcher.appendTail(sb);
		LOG.debug("emojiConvert " + str + " to " + sb.toString() + ", len：" + sb.length());
		return sb.toString();
	}

	public static String filterEmojiPro(String source) {
		if (!containsEmoji(source)) {
			return source;// 如果不包含，直接返回
		}

		StringBuilder buf = null;
		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);
			if (!isEmojiCharacter(codePoint)) {
				if (buf == null) {
					buf = new StringBuilder(source.length());
				}
				buf.append(codePoint);
			} else {
			}
		}
		if (buf == null) {
			return "";
		} else {
			if (buf.length() == len) {// 这里的意义在于尽可能少的toString，因为会重新生成字符串
				buf = null;
				return source;
			} else {
				return buf.toString();
			}
		}
	}

	// 判别是否包含Emoji表情
	private static boolean containsEmoji(String str) {
		int len = str.length();
		for (int i = 0; i < len; i++) {
			if (isEmojiCharacter(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	private static boolean isEmojiCharacter(char codePoint) {
		return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
				|| ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
				|| ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
	}
	
	/** 
     * 判断特殊字符 
     * @Title : FilterStr 
     * @Type : FilterString 
     * @date : 2014年2月28日 下午11:01:21 
     * @Description : 判断特殊字符 
     * @param str 
     * @return 
     * @throws PatternSyntaxException 
     */  
    public static String FilterStr(String str)
    {  
        /** 
         * 特殊字符 
         */  
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？_]";  
          
        /** 
         * Pattern p = Pattern.compile("a*b"); 
         * Matcher m = p.matcher("aaaaab"); 
         * boolean b = m.matches(); 
         */  
        Pattern pat = Pattern.compile(regEx);       
        Matcher mat = pat.matcher(str);  
          
        /** 
         * 返回替换结果 
         */  
        return mat.replaceAll("").trim();    
    }  
  

	public static void main(String[] args) {
		String str = "快更新啦😄❤";
		System.err.println(EmojiCodeUtil.emojiExtract(str));

		String strH = "Hi ";
		System.err.println(StringUtils.containsIgnoreCase(strH, "hi"));
		System.err.println(str);
		System.err.println(EmojiCodeUtil.FilterStr(str));
		SimpleDateFormat sft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.err.println(sft.format(new Date()));
	}

}
