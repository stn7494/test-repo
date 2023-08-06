package ez.en.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.domain.AttachFileDTO;

import lombok.extern.log4j.Log4j;
import net.coobird.thumbnailator.Thumbnailator;

@Controller
@Log4j
public class UploadController {
	
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	
	@GetMapping("/uploadForm")
	public String uploadFor() {
		return "uploadForm";
	}
	
	@PostMapping("/uploadFormAction")
	public String uploadForm(MultipartFile[] uploadFile, Model model, HttpServletRequest request) {
		String uploadFolder2 = request.getServletContext().getRealPath("/")+"resources/img";
		logger.info(uploadFolder2);
		String uploadFolder = "C:\\upload";
		for (MultipartFile multipartFile : uploadFile) {
			logger.info("Upload File Name: "+multipartFile.getOriginalFilename());
			logger.info("Upload File Size: "+multipartFile.getSize());
			File saveFile = new File(uploadFolder, multipartFile.getOriginalFilename());
			
			try {
				multipartFile.transferTo(saveFile);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return "img";
	}
	
	@GetMapping("/uploadAjax")
	public void uploadAjax() {
		logger.info("upload ajax");
	}
	
	@GetMapping("/uploadAjax2")
	public void uploadAjax2() {
		logger.info("upload ajax2");
	}
	
	@PostMapping(value = "uploadAjaxAction", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public ResponseEntity<List<AttachFileDTO>> uploadAjaxPost(MultipartFile[] uploadFile, HttpServletRequest request) {
		
		List<AttachFileDTO> list = new ArrayList<>();
		
		logger.info("update ajax post.........");
		
//		서버에 있는 경로로 파일을 저장하기 위해서 request가 필요함
		String uploadFolder1 = request.getServletContext().getRealPath("/")+"resources/img";
//		오늘 날짜의 폴더를 
		String uploadFolderPath = getFolder(); 
		File uploadPath = new File(uploadFolder1, getFolder());
//		업로드의 위치를 지정 C:\SDH\springworkspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\FileUpload\resources\img\2023\08\01
		logger.info("upload path:" + uploadPath);
		
//		만약에 C:\SDH\springworkspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\FileUpload\resources\img\
//		경로에 2323\08\01폴더가 없다면
		if(uploadPath.exists() == false) {
//			경로에 맞게 폴더 생성
			uploadPath.mkdirs();
		}
		
		for (MultipartFile multipartFile : uploadFile) {
			
			AttachFileDTO attachDTO = new AttachFileDTO();
			
			logger.info("---------------------------------------");
			logger.info("Upload File Name:" + multipartFile.getOriginalFilename());
			logger.info("Upload File Size:" + multipartFile.getSize());
			
			String uploadFileName = multipartFile.getOriginalFilename();
			
//			익스플로어의 경우 아래에 있는 방법으로 잘라줘야함
			uploadFileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\") +1);
			
			logger.info("only file name : "+ uploadFileName);
			
			attachDTO.setFileName(uploadFileName);
			
//			랜덤한 숫자와 영어를 조합해줌
			UUID uuid = UUID.randomUUID();
			
			uploadFileName = uuid.toString() + "_" + uploadFileName;
			
			
			try {
//				내가 파일을 업로드할 장소와 파일의 이름 설정
				File savaFile = new File(uploadPath, uploadFileName);
//				내가 업로드한 파일을 폴더에 저장
				multipartFile.transferTo(savaFile);
				
				
				attachDTO.setUuid(uuid.toString());
				attachDTO.setUploadPath(uploadFolderPath);
				
				
				// img파일인지 체크
				if(checkImageType(savaFile)) {
					attachDTO.setImage(true);
					
//					업로드될 파일의 섬네일 파일의 경로와 파일의 이름 생성
					FileOutputStream thumbnail = new FileOutputStream(new File(uploadPath, "s_" + uploadFileName));

//					섬네일을 만들어주는 라이브러리에서 기능을 활용(섬네일로 만들 파일의 정보, 파일이 저장될 위치와 이름, 파일의 높이, 너비)
					Thumbnailator.createThumbnail(multipartFile.getInputStream(), thumbnail, 100, 100);
					
					thumbnail.close();
				}
				list.add(attachDTO);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	
	private String getFolder() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
//		오늘 날짜를 date에 담음
		Date date = new Date();
		
//		위에 지정한 데이터 타입으로 오늘날짜를 변경 ex) 2023-08-01
		String str = sdf.format(date);

//		"-"를 \로 전환
		return str.replace("-", File.separator);
		
	}
	
	private boolean checkImageType(File file) {
		try {
			String contentType = Files.probeContentType(file.toPath());
			
			return contentType.startsWith("image");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	@GetMapping("/display")
	@ResponseBody
	public ResponseEntity<byte[]> getFile(@RequestParam("fileName") String fileName, HttpServletRequest request){
		
		logger.info("fileName: "+fileName);
		String uploadFolder1 = request.getServletContext().getRealPath("/")+"resources/img";
		File file = new File(uploadFolder1+ fileName);
		
		logger.info("file: "+ file);
		
		ResponseEntity<byte[]> result = null;
		
		HttpHeaders header = new HttpHeaders();
		
		try {
			header.add("Content-Type", Files.probeContentType(file.toPath()));
			result = new  ResponseEntity<>(FileCopyUtils.copyToByteArray(file),
					header, HttpStatus.OK);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
		
	}
	@PostMapping("uploadAjaxAction2")
	@ResponseBody
	public void uploadAjaxPost2(MultipartFile[] uploadFile, HttpServletRequest request) {
		
		logger.info("update ajax post.........");
		
		String uploadFolder1 = "C:\\upload";
		
		for (MultipartFile multipartFile : uploadFile) {
			
			logger.info("---------------------------------------");
			logger.info("Upload File Name:" + multipartFile.getOriginalFilename());
			logger.info("Upload File Size:" + multipartFile.getSize());
			
			String uploadFileName = multipartFile.getOriginalFilename();
			
			uploadFileName = "user01_"+ uploadFileName;
			try {
				File savaFile = new File(uploadFolder1, uploadFileName);
				multipartFile.transferTo(savaFile);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
