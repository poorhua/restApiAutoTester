package com.autotest.restApiTest;
  
import java.util.List;
import java.util.ArrayList;

import org.testng.TestNG;
 

public class AutoTester {
	public static void main(String agrc[]) {
		if(agrc.length <=0) {
			System.out.println("error: need a argument.");
			return;
		}
		TestNG testNG = new TestNG();
		List<String> suites =  new ArrayList<String>();
        suites.add(agrc[0]);
        testNG.setTestSuites(suites);
        testNG.run();
	} 
}
