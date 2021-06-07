package com.xxl.job.admin;

import com.google.gson.Gson;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * XxlJob开发示例（Bean模式）
 * <p>
 * 开发步骤：
 * 1、任务开发：在Spring Bean实例中，开发Job方法；
 * 2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 * 4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 *
 * @author xuxueli 2019-12-11 21:52:51
 */
@Component
public class SampleXxlJob {
    private static Logger logger = LoggerFactory.getLogger(SampleXxlJob.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;


    /**
     * Bean模式
     */
    @XxlJob("demoJobHandler")
    public void demoJobHandler() throws Exception {
        XxlJobHelper.log("job start:" + System.currentTimeMillis());
        String param = XxlJobHelper.getJobParam();
        JobRequest jobRequest = new Gson().fromJson(param, JobRequest.class);

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        HttpEntity<String> formEntity = new HttpEntity<>(jobRequest.getBody(), headers);

        String url = jobRequest.getUrl();

        int start = url.indexOf("//") + 2;
        int end = url.indexOf("/", start);
        String serviceName = url.substring(start, end);

        List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances(serviceName);

        if (!serviceInstanceList.isEmpty()) {
            ServiceInstance serviceInstance = serviceInstanceList.get(0);
            url = serviceInstance.getUri().toString()+url.substring(end);
//            String ip = serviceInstance.getHost();
//            int port = serviceInstance.getPort();
//            url = url.substring(0, start) + ip + ":" + port + url.substring(end);
        }

        String result = restTemplate.postForObject(url, formEntity, String.class);

        XxlJobHelper.log("job result:" + result);
        XxlJobHelper.log("job end:" + System.currentTimeMillis());
    }

}
