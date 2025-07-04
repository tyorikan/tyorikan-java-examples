package com.example.action;

import com.example.service.HelloService;
import com.opensymphony.xwork2.ActionProxy;
import org.apache.struts2.StrutsJUnit4TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class HelloActionTest extends StrutsJUnit4TestCase<HelloAction> {

    @Autowired
    private HelloService helloService;

    @Override
    protected String getConfigPath() {
        return "struts.xml";
    }

    @Test
    public void testExecute() throws Exception {
        // Actionのインスタンスを取得
        ActionProxy proxy = getActionProxy("/hello.action");
        HelloAction action = (HelloAction) proxy.getAction();

        // Serviceをセット
        action.setHelloService(helloService);

        // パラメータをセット
        action.setName("Test");

        // Actionを実行
        String result = proxy.execute();

        assertEquals("success", result);
        assertEquals("Hello, Test!", action.getMessage());
    }
}