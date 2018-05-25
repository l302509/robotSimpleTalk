package MySpringBoot.parentDemo.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebToolUtils {
	private static Logger log = LoggerFactory.getLogger(WebToolUtils.class);
	
	
	
	/**
	 * linux系统，此方法获取不正确,windows系统会抛异常
     * 获取服务器IP地址
     * @return
     */
    public static String  getServerIp(){
        String SERVER_IP = null;
        try {
            Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                ip = (InetAddress) ni.getInetAddresses().nextElement();
                SERVER_IP = ip.getHostAddress();
                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                        && ip.getHostAddress().indexOf(":") == -1) {
                    SERVER_IP = ip.getHostAddress();
                    break;
                } else {
                    ip = null;
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
        return SERVER_IP;
    }
    
    /**
     * 获取Linux下的IP地址
     *
     * @return IP地址
     * @throws SocketException
     */
    public static String getLinuxLocalIp(){
      String ip = "";
      try {
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
          NetworkInterface intf = en.nextElement();
          String name = intf.getName();
          if (!name.contains("docker") && !name.contains("lo")) {
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
              InetAddress inetAddress = enumIpAddr.nextElement();
              if (!inetAddress.isLoopbackAddress()) {
                String ipaddress = inetAddress.getHostAddress().toString();
                if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
                  ip = ipaddress;
                  //System.out.println(ipaddress);
                }
              }
            }
          }
        }
      } catch (SocketException ex) {
    	  log.error("获取ip地址异常",ex);
    	  ip = "127.0.0.1";
      }
      return ip;
    }
    
    public static String getHostName(){
    	String hostName = null;
    	try {
    		InetAddress i = InetAddress.getLocalHost();
    		hostName = i.getHostName();
    	} catch (UnknownHostException e) {
    		log.error("获取ip地址异常",e);
    	}  
    	
    	
    	return hostName;
    	
    }
    public static String getIP(){
    	String hostIP = null;
		try {
			InetAddress i = InetAddress.getLocalHost();
			hostIP = i.getHostAddress();
		} catch (UnknownHostException e) {
			log.error("获取ip地址异常",e);
		}  
		return hostIP;
    	
    }
    
    /** 
     * 获取当前网络ip 
     * @param request 
     * @return 
     */  
    public static String getIpAddr(HttpServletRequest request){
    	/*ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();*/
        String ipAddress = request.getHeader("x-forwarded-for");  
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
                ipAddress = request.getHeader("Proxy-Client-IP");  
            }  
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
                ipAddress = request.getHeader("WL-Proxy-Client-IP");  
            }  
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
                ipAddress = request.getRemoteAddr();  
                if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){  
                    //根据网卡取本机配置的IP  
                    InetAddress inet=null;  
                    try {  
                        inet = InetAddress.getLocalHost();  
                    } catch (UnknownHostException e) {  
                        e.printStackTrace();  
                    }  
                    ipAddress= inet.getHostAddress();  
                }  
            }  
            //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割  
            if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15  
                if(ipAddress.indexOf(",")>0){  
                    ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));  
                }  
            }  
            return ipAddress;   
    }
    
    
    public static String getIpAddrNginx(HttpServletRequest request) {  
        String ip = request.getHeader("X-Real-IP");//先从nginx自定义配置获取  
        String REMOTE_HOST = request.getHeader("REMOTE-HOST");
        String X_Forwarded_For = request.getHeader("X-Forwarded-For");
        String Host = request.getHeader("Host");
        String local_id = getIP();
        String referer = request.getHeader("referer");
        /*System.err.println("----------X-Real-IP:"+ip);
        System.err.println("----------REMOTE_HOST:"+REMOTE_HOST);
        System.err.println("----------X_Forwarded_For:"+X_Forwarded_For);
        System.err.println("----------Host:"+Host);
        System.err.println("----------local_id:"+local_id);
        System.err.println("----------referer:"+referer);*/
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("X-Forwarded-For");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
        }  
        return ip;  
    }
    
    public static String getHeaderReferer(HttpServletRequest request,String path){
    	String referer = request.getHeader("referer");
    	log.info("中台请求referer：{}",referer);
    	if(StringUtils.isNotBlank(referer)){
    		if( referer.indexOf(path) > 0){
    			referer = referer.substring(0,referer.indexOf(path)+path.length());
    		}else{
    			referer = null;
    		}    		
    	}
    	//System.err.println("=============>"+referer);
    	log.info("中台请求referer_after：{}",referer);
		return referer;
    	
    }

}
