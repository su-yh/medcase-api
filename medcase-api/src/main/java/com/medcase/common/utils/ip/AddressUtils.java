package com.medcase.common.utils.ip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.medcase.common.constant.Constants;
import com.medcase.common.utils.StringUtils;
import com.medcase.common.utils.http.HttpUtils;
import com.medcase.common.utils.json.JsonUtils;

/**
 * 获取地址类
 */
public class AddressUtils {

    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

    private static boolean addressEnabled;

    // IP地址查询
    public static final String IP_URL = "https://whois.pconline.com.cn/ipJson.jsp";

    // 未知地址
    public static final String UNKNOWN = "XX XX";

    public static void setAddressEnabled(boolean addressEnabled) {

        AddressUtils.addressEnabled = addressEnabled;
    }

    public static String getRealAddressByIP(String ip) {

        // 内网不查询
        if (IpUtils.internalIp(ip)) {

            return "内网IP";
        }
        if (addressEnabled) {

            try {

                String rspStr = HttpUtils.sendGet(IP_URL, "ip=" + ip + "&json=true", Constants.GBK);
                if (StringUtils.isEmpty(rspStr)) {

                    log.error("获取地理位置异常 {}", ip);
                    return UNKNOWN;
                }
                String region = JsonUtils.readTree(rspStr).path("pro").asText();
                String city = JsonUtils.readTree(rspStr).path("city").asText();
                return String.format("%s %s", region, city);
            }
            catch (Exception e) {

                log.error("获取地理位置异常 {}", ip);
            }
        }
        return UNKNOWN;
    }
}
