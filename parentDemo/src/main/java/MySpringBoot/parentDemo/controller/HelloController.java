package MySpringBoot.parentDemo.controller;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import MySpringBoot.parentDemo.domain.constants.AnswersResults;
import MySpringBoot.parentDemo.domain.message.MarkdownMessage;
import MySpringBoot.parentDemo.utils.EmojiCodeUtil;

@RestController
@RequestMapping(value = "/hello")
public class HelloController {
	private static Logger log = LoggerFactory.getLogger(HelloController.class);
	
	//private static Map<String,String> ansMap = new ConcurrentHashMap<>();
	
	@RequestMapping(value = "/say")
	public String sayHelloWorld(String str) {
		log.info("input:{}",str);
		String out = "你好！";
		if(str.trim().startsWith("教学模式")){
			String content = str.trim().replace("教学模式", "");
			String question = content.substring(content.indexOf("问题")+2,content.indexOf("回答"));
			String answer = content.substring(content.indexOf("回答")+2);
			question = replaceStr(question);
			answer = replaceStr(answer);
			List reulstList = AnswersResults.getMaxRateValue(question);
			float rate = (float) reulstList.get(0);
			if(rate < 0.6){
				//将新的问答插入xml
				AnswersResults.addAnswerValue(question,answer);
				//同时将新的问答插入AnswersResults.ansMap
				List<String> answerList = new ArrayList<>();
				answerList.add(answer);
				AnswersResults.ansMap.put(question, answerList);
			}else{
				String key = (String) reulstList.get(1);
				List<String> answerList = (List<String>) reulstList.get(2);
				//将新的问答插入xml
				AnswersResults.addAnswerValue(key,answer);
				//同时将新的问答插入AnswersResults.ansMap
				answerList.add(answer);
				AnswersResults.ansMap.put(key, answerList);
			}
			out = "好的，知道了！你可以试着问一下："+question;
			log.info("output:{}",out);
			return out;
		}		
		String filterStr = EmojiCodeUtil.filterEmojiPro(str);
		String emoji = EmojiCodeUtil.emojiExtract(str);
		for(Entry<String, List<String>> entry : AnswersResults.ansMap.entrySet()){
			List reulstList = null;
			if(null == filterStr || "".equals(filterStr)){
				reulstList = AnswersResults.getMaxRateValue(emoji);
			}else{
				reulstList = AnswersResults.getMaxRateValue(filterStr);
			}
			 
			float rate = (float) reulstList.get(0);
			if(rate > 0.60){
				List strList = (List) reulstList.get(2);
				int index = Math.abs( (new Random()).nextInt() )%strList.size();
				out = (String) strList.get(index);
			}else{
				out = "我不知道怎么回答，你可以教教我。像下面这样：<br/> 教学模式<br/>问题：你叫什么？回答：我叫小蓝。";
			}
			log.info("output:{}",out);
			return out;
		}
		if(null == filterStr || "".equals(filterStr)){
			if( null != emoji){
				return emoji;
			}
		}else if("你好".equals(filterStr) || filterStr.contains("你好") || StringUtils.containsIgnoreCase(filterStr,"hi" ) || StringUtils.containsIgnoreCase(filterStr,"hello" )){
			
		}else if(filterStr.contains("你叫什么")){
			out = "我叫小蓝";
		}else if(filterStr.contains("傻子") || filterStr.contains("傻") ){
			out = "你才是傻子。。。";
		}else if(filterStr.contains("几点") || filterStr.contains("时间")){
			SimpleDateFormat sft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			out = "现在时间："+ sft.format(new Date());
		}else if("markdown".equals(filterStr)){
			try {
				out = makedown();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if("我爱你".equals(filterStr)){
			out = "I love you too!❤";
		}else {
			//out = "你说什么？我没听清楚，你再说一遍。。。";
			out = "我不知道怎么回答，你可以教教我。像下面这样：<br/><br/> 教学模式<br/>问题：你叫什么？回答：我叫小蓝。";
		}
		if(filterStr.contains("爱")){
			out = out.concat("❤");
		}
			
		log.info("output:{}",out);
		return  emoji+out;
	}
	
	private String makedown() throws Exception{
		MarkdownMessage message = new MarkdownMessage();
        message.setTitle("This is a markdown message");

        message.add(MarkdownMessage.getHeaderText(1, "header 1"));
        message.add(MarkdownMessage.getHeaderText(2, "header 2"));
        message.add(MarkdownMessage.getHeaderText(3, "header 3"));
        message.add(MarkdownMessage.getHeaderText(4, "header 4"));
        message.add(MarkdownMessage.getHeaderText(5, "header 5"));
        message.add(MarkdownMessage.getHeaderText(6, "header 6"));

        message.add(MarkdownMessage.getReferenceText("reference text"));
        message.add("\n\n");

        message.add("normal text");
        message.add("\n\n");

        message.add(MarkdownMessage.getBoldText("Bold Text"));
        message.add("\n\n");

        message.add(MarkdownMessage.getItalicText("Italic Text"));
        message.add("\n\n");

        ArrayList<String> orderList = new ArrayList<String>();
        orderList.add("order item1");
        orderList.add("order item2");
        message.add(MarkdownMessage.getOrderListText(orderList));
        message.add("\n\n");

        ArrayList<String> unorderList = new ArrayList<String>();
        unorderList.add("unorder item1");
        unorderList.add("unorder item2");
        message.add(MarkdownMessage.getUnorderListText(unorderList));
        message.add("\n\n");

        message.add(MarkdownMessage.getImageText("http://img01.taobaocdn.com/top/i1/LB1GCdYQXXXXXXtaFXXXXXXXXXX"));
        message.add(MarkdownMessage.getLinkText("This is a link", "dtmd://dingtalkclient/sendMessage?content=www.baidu.com"));
        message.add(MarkdownMessage.getLinkText("中文跳转", "dtmd://dingtalkclient/sendMessage?content=" + URLEncoder.encode("链接消息", "UTF-8")));
		return message.toJsonString();
		
	}
	
	private String replaceStr(String str){
		String out = str;
		if(str.trim().indexOf(":")>-1){
			out = str.replace(":", "");
		}
		if(str.trim().indexOf("：")>-1){
			out = str.replace("：", "");
		}
		return out;
	}

}
