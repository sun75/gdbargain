package com.gdbargain.gdthirdparty.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.gdbargain.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: shh
 * @createTime: 2023/1/910:39
 */
@RestController //将响应体Response以json格式返回回去
@RequestMapping("/gdthirdparty")
public class OssController {

//    @Autowired
//    OSS ossClient;

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endPoint;

    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucket;

    @Value("${spring.cloud.alicloud.access-key}")
    private String accessId;

    @Value("${spring.cloud.alicloud.secret-key}")
    private String accessKeySecret;



//    要对象存储的相关签名数据
    @RequestMapping("/oss/policy")
    public R policy(){
        String host = "https://"+bucket+"."+endPoint;
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dir = format+"/";
        Map<String, String> respMap = null;
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessId, accessKeySecret);

        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            respMap = new LinkedHashMap<String, String>();
            respMap.put("accessId", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));

            //map里面放一些属性数据，这些数据最终用跨域的方式以response响应出去
            //但是跨域是统一解决


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //因为前端beforeUpload()方法内
        // 给dataObj的各个属性赋值：_self.dataObj.policy = response.data.policy;
        //需要response里面的data值，但是console是直接返回值，没有data
        //所以返回值由map改为R类型
        return R.ok().put("data",respMap);
    }
}
