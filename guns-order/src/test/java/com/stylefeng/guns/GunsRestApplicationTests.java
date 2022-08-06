package com.stylefeng.guns;

import com.stylefeng.guns.rest.OrderApplication;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderApplication.class)
public class GunsRestApplicationTests {

	@Resource
	private FTPUtil ftpUtil;

	@Test
	public void contextLoads() {
		String fileStrByAddress = ftpUtil.getFileStrByAddress("seats/cgs.json");
		System.out.println(fileStrByAddress);
	}

}
