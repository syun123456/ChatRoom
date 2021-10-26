package com.syun.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public class UploadFileUtils {
	
	// 設置頭貼
	public void setAvater(String username, MultipartFile file){
		try {
			// 取得當前項目位置
			Resource resource = new ClassPathResource("static/img/");
			
			// 頭像實際存放位置
			String path = resource.getFile().getAbsolutePath() + "\\" + username;
			
			// 如果路徑不存在，則建立一個
			File realPath = new File(path);
			if(!realPath.exists()) {
				realPath.mkdir();
			}
			
			InputStream is = null;
			
			// file為null則使用預設的頭貼
			if(file == null) {
				is = new FileInputStream("C:\\Workspace\\ChatRoom\\src\\main\\resources\\static\\img\\avatar.jpg");
			}else {
				is = file.getInputStream();
			}
			
			this.uploadFile("avatar.jpg", realPath, is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 使用者上傳頭貼
	public String uploadAvater(String username, MultipartFile file) {
		// 取得上傳檔案名稱
		String uploadFileName = file.getOriginalFilename();
		
		if("".equals(uploadFileName)) {
			return "檔案或檔名不得為空，請重新選取";
		}
		
		String[] uploadFileNameArr = uploadFileName.split("\\.");
		
		// 取得上傳檔案的副檔名
		String uploadFileNameExtension = uploadFileNameArr[uploadFileNameArr.length-1];
		
		// 存放支援的檔案格式
		List<String> fileExtension = Arrays.asList("jpg", "jpeg", "png");
		
		// 確認上傳檔案的格式是否正確
		if(!fileExtension.contains(uploadFileNameExtension)) {
			return "圖片僅支援jpg、jpeg、png格式，請重新上傳";
		}
		
		setAvater(username, file);
		
		return "頭貼上傳成功";
	}
	
	public void uploadFile(String fileName, File realPath , InputStream is) throws IOException {
		OutputStream os = new FileOutputStream(new File(realPath, fileName));
		
		int len = 0;
		byte[] buffer = new byte[1024];
		while((len=is.read(buffer))>0) {
			os.write(buffer,0,len);
			os.flush();
		}
		
		os.close();
	}
}
