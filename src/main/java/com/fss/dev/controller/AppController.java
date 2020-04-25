package com.fss.dev.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fss.dev.bean.DataModel;
import com.fss.dev.bean.MastercardModelMapper;
import com.fss.dev.bean.OutputModel;
import com.fss.dev.common.util.LoggerUtil;
import com.fss.dev.service.AppService;
import com.fss.dev.util.ApiResponse;
import com.fss.dev.util.ApiUtil;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class AppController {
	
	
	LoggerUtil logger = new LoggerUtil(AppController.class);
	
	@Value("${app.download.directory}")
	private String downloadDirectory;
	
	@Autowired
	AppService appService;

	@GetMapping("/app-status")
	@ApiOperation(value = "This is used to check the status of Microservice", notes="Doesn't require any input",response=String.class)
	public ApiResponse hello() {
		ApiResponse response =  new ApiResponse();
		Date today = new Date();
		response.setApiResponse("API is Active!!" +today);
		response.setApiError("");
		response.setStatus(ApiResponse.STATUS_SUCCESS);
		return response;
	}
	
	@PostMapping("/mastercard-incoming-parser")
	@ApiOperation(value = "This Api helps in parsing Master card Incoming file ", notes="It takes Mastercard Incoming file as Input",response=ApiResponse.class)
	 public @ResponseBody  ApiResponse readJCBFiles(@RequestParam("Mastercard-Incoming-File") MultipartFile uploadfile) {
		ApiResponse response = new ApiResponse(); 
		InputStream inStream = null;
		 boolean trailerFound = false;
		 String msTranType = "";
//		 OutputStream fw;
		 List<List<Map<String,String>>> output = new ArrayList<>();
		 int recordsProcessCount=0;
		 try{
			 String filePath=downloadDirectory+uploadfile.getOriginalFilename();
			 
			 File downDir = new File(downloadDirectory);
			 if(!downDir.exists()) {
				 downDir.mkdirs();
			 }
			 //save file to local
			 if(!uploadfile.isEmpty()) {
				 byte[] bytes = uploadfile.getBytes();
		         Path path = Paths.get(downloadDirectory + uploadfile.getOriginalFilename());
		         Files.write(path, bytes);
			 }else {
				 response.setApiResponse("");
				 response.setApiError("Please upload valid Mastercard Incoming File");
				 response.setStatus(ApiResponse.STATUS_FAIL);
				 return response;
			 }
			 ApiUtil.CorrectFile(filePath, filePath+"_correct");			 			 			 		
			 filePath = filePath.concat("_correct");			 			 			 		
			 inStream = new FileInputStream(new File(filePath));
			 byte[] marrCharMap = new byte[16];
			 byte[] transactType = new byte[4]; 
			 byte[] skipBits = new byte[4];
			 byte[] skipBitsss = new byte[23];
			List<MastercardModelMapper> mappingFileCollection = appService.populateData(); 
			 while(!trailerFound){
				 List<Map<String,String>> item = new ArrayList<>();
				 //skip first 4 bytes
				     inStream.read(skipBits);
				     String skipmsBits = new String(skipBitsss,"ISO-8859-1");
				     char[] skipcharrBits = skipmsBits.toCharArray();
				     String skipmsBitmap = ApiUtil.getBITMAP(skipcharrBits);
				     	
				 //read 4 bytes after that
				 	inStream.read(transactType);
				 	msTranType = new String(transactType);	
				 	
				 //read 16 bytes of data
				 	int data = inStream.read(marrCharMap);
				 	String msBits = new String(marrCharMap,"ISO-8859-1");
				 	char[] charrBits = msBits.toCharArray();
				 	String msBitmap = ApiUtil.getBITMAP(charrBits);
				 	logger.debug("bitmap "+msBitmap);
				 
				 	OutputModel outputModel = null;
				 	outputModel =  appService.setIPMData(mappingFileCollection,msBitmap, msTranType, inStream);
				 	if(outputModel!=null){
				 		List<DataModel> mainList = outputModel.getMainList();
				 		List<List<DataModel>> Sublist = outputModel.getSubLists();
				 		Map<String,String> map = new HashMap<>();
				 		if(!mainList.isEmpty()) {
				 			for(DataModel model : mainList) {
				 				if(model.getColumn().equalsIgnoreCase("MIM_FUNC_CODE") && model.getValue().equalsIgnoreCase("695"))
					 				trailerFound = true;
				 				map.put(model.getColumn(), model.getValue());	
				 			}
				 		}
				 		item.add(map);
				 		output.add(item);
				 		
				 		// This is for Multiple occurrences of PDS elements 
				 		if(Sublist!=null) {
				 			List<Map<String,String>> allSubList = new ArrayList<>();
				 			Map<String,String> subMap = new HashMap<>();
					 		if(!Sublist.isEmpty()) {
					 			for(int i=0;i<Sublist.size();i++) {
					 			List<DataModel> subSubList = Sublist.get(i);
					 			for(DataModel model : subSubList) {
					 				subMap.put(model.getColumn(), model.getValue());	
					 			}
					 			allSubList.add(subMap);
					 			}
					 		}
					 		output.add(allSubList);
				 		}
				 	}
				 	recordsProcessCount++;
			 }		
			 logger.info("Total number of records processed: "+recordsProcessCount);
			 response.setStatus(ApiResponse.STATUS_SUCCESS);
			 response.setApiResponse(output);
			 response.setApiError("");
			 return response;
		 }catch(Exception e){
			 logger.info("Total number of records processed: "+recordsProcessCount);
			 logger.error("Error :",e);
			 response.setStatus(ApiResponse.STATUS_FAIL);
			 response.setApiResponse("");
			 response.setApiError("Failed to parse Mastercard Incoming file. Error occured in record Number :"+(recordsProcessCount+1));
			 return response;
		 }finally{
			 if(inStream!=null)
				try {
					inStream.close();
				} catch (IOException e) {
					logger.error("Err ",e);
					
				}
		 }
	 }
	
}
