package com.autotest.restApiTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
//import org.testng.annotations.AfterTest;
//import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
//import org.testng.annotations.Test;
import org.testng.annotations.Test;

import com.autotest.utils.DataReader;
import com.autotest.utils.DataWriter;
import com.autotest.utils.HTTPReqGen;
import com.autotest.utils.RecordHandler;
import com.autotest.utils.SheetUtils;
//import com.autotest.utils.Utils;
import com.jayway.restassured.response.Response;

public class HTTPReqGenTest implements ITest {

    private Response response;
    private DataReader myInputData;
    private DataReader myBaselineData;
    private String template;

    public String getTestName() {
        return "API Test";
    }
    
    String filePath = "";
    
    XSSFWorkbook wb = null;
    XSSFSheet inputSheet = null;
    XSSFSheet baselineSheet = null;
    XSSFSheet outputSheet = null;
    XSSFSheet comparsionSheet = null;
    XSSFSheet resultSheet = null;
    
    private double totalcase = 0;
    private double failedcase = 0;
    private String startTime = "";
    private String endTime = "";

    
    @BeforeClass
    @Parameters("workBook")
    public void setup(String path) {
        filePath = path;
        try {
            wb = new XSSFWorkbook(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputSheet = wb.getSheet("Input");
        baselineSheet = wb.getSheet("Baseline");
        
        SheetUtils.removeSheetByName(wb, "Output");
        /*if(wb.getSheetIndex("Output")>=0)
        wb.removeSheetAt("Output");
        */	
        SheetUtils.removeSheetByName(wb, "Comparison");
        /*if(wb.getSheetIndex("Comparison")>=0)
        wb.removeSheetAt("Comparison");
        */	
        SheetUtils.removeSheetByName(wb, "Result");
        /*if(wb.getSheetIndex("Result")>=0)
        wb.removeSheetAt(wb.getSheetIndex("Result"));
        */	
        outputSheet = SheetUtils.createSheet(wb,"Output");
        comparsionSheet = SheetUtils.createSheet(wb,"Comparison");
        resultSheet = SheetUtils.createSheet(wb,"Result");

        try {
            InputStream is = HTTPReqGenTest.class.getClassLoader().getResourceAsStream("http_request_template.txt");
            //template = IOUtils.toString(is, Charset.defaultCharset()); //JDK 1.8 无效
            InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset());
            char[] ch=new char[1024];
            int len=isr.read(ch);
            template = (new String(ch,0,len));
            isr.close();
        } catch (Exception e) {
            Assert.fail("Problem fetching data from input file:" + e.getMessage());
        }

        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        startTime = sf.format(new Date());
    }

    @DataProvider(name = "WorkBookData")
    protected Iterator<Object[]> testProvider(ITestContext context) {

        List<Object[]> test_IDs = new ArrayList<Object[]>();

            myInputData = new DataReader(inputSheet, true, true, 0);
            Map<String, RecordHandler> myInput = myInputData.get_map();

            // sort map in order so that test cases ran in a fixed order
            //Map<String, RecordHandler> sortmap = Utils.sortmap(myInput);
            Map<String, RecordHandler> sortmap = new TreeMap<String, RecordHandler>(myInput);
            for (Map.Entry<String, RecordHandler> entry : sortmap.entrySet()) {
                String test_ID = entry.getKey();
                String test_case = entry.getValue().get("TestCase");
                if (!test_ID.equals("") && !test_case.equals("")) {
                    test_IDs.add(new Object[] { test_ID, test_case });
                }
                totalcase++;
            }
            
            myBaselineData = new DataReader(baselineSheet, true, true, 0);

        return test_IDs.iterator();
    }

    @Test(dataProvider = "WorkBookData", description = "ReqGenTest")
    public void api_test(String ID, String test_case) {

        HTTPReqGen myReqGen = new HTTPReqGen();

        try {
            myReqGen.generate_request(template, myInputData.get_record(ID));
            response = myReqGen.perform_request();
        } catch (Exception e) {
            Assert.fail("Problem using HTTPRequestGenerator to generate response: " + e.getMessage());
        }
        
        String baseline_message = myBaselineData.get_record(ID).get("Response");

        if (response.statusCode() == 200)
            try {
            	//记录输出结果 到输出页
                DataWriter.writeData(outputSheet, response.asString(), ID, test_case);
                
                //结果与BASELINE中值比对（JSONCompareResult格式）
                JSONCompareResult result = JSONCompare.compareJSON(baseline_message, response.asString(), JSONCompareMode.NON_EXTENSIBLE);
                
                //值为不通过 则记录成功失败信息，否则记录结果信息
                if (!result.passed()) {
                    DataWriter.writeData(comparsionSheet, result, ID, test_case);
                    DataWriter.writeData(resultSheet, "false", ID, test_case, 0);
                    DataWriter.writeData(outputSheet); //??
                    failedcase++;
                } else {
                    DataWriter.writeData(resultSheet, "true", ID, test_case, 0);
                }
            } catch (JSONException e) {
            	//结果解析出错，记录错误信息和失败信息
                DataWriter.writeData(comparsionSheet, "", "Problem to assert Response and baseline messages: "+e.getMessage(), ID, test_case);
                DataWriter.writeData(resultSheet, "error", ID, test_case, 0);
                failedcase++;
                Assert.fail("Problem to assert Response and baseline messages: " + e.getMessage());
            }
        else {
        	//网络或应用错误，记录错误信息和失败信息
            DataWriter.writeData(outputSheet, response.statusLine(), ID, test_case);

            if (baseline_message.equals(response.statusLine())) {
                DataWriter.writeData(resultSheet, "true", ID, test_case, 0);
            } else {
                DataWriter.writeData(comparsionSheet, baseline_message, response.statusLine(), ID, test_case);
                DataWriter.writeData(resultSheet, "false", ID, test_case, 0);
                DataWriter.writeData(outputSheet); 
                failedcase++;
            }
        }
    }

    @AfterClass
    public void teardown() {
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        endTime = sf.format(new Date());
        DataWriter.writeData(resultSheet, totalcase, failedcase, startTime, endTime); 
        
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            wb.write(fileOutputStream);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}