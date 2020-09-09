package com.lk.taskmanager.apis;

import com.lk.taskmanager.entities.ResourceInfoEntity;
import com.lk.taskmanager.security.ExtractAuthUser;
import com.lk.taskmanager.services.domain.storage.ResourceInfoService;
import com.lk.taskmanager.services.generic.GenericResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("rest/resource")
public class ResourceController {

	private final ResourceInfoService resourceInfoService;

	public ResourceController(ResourceInfoService resourceInfoService) {
		this.resourceInfoService = resourceInfoService;
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/user-resources")
	public ResponseEntity<?> getAllResourceByUserId(@PageableDefault(page = 0, size = 10) Pageable pageable, @RequestParam("userId") Long userId) {
		userId = ExtractAuthUser.resolveHasAdminAccessOrIsOwner(userId);
		log.info("Resource API: get all resources by user id: ", userId);
		List<ResourceInfoEntity> resourceInfoList = resourceInfoService.getAllResourceByOwnerId(pageable, userId);
		return new ResponseEntity<>(resourceInfoList, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@PostMapping("/upload-resource")
	public ResponseEntity<?> uploadResource(@RequestParam("resource") MultipartFile file) {
		log.info("Resource API: upload resource");
		GenericResponseDTO<?> genericResponse = resourceInfoService.storeResource(file);
		return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteResource(@PathVariable("id") Long resourceId) {
		log.info("Resource API: delete resource by resource id: ", resourceId);
		GenericResponseDTO<?> genericResponse = resourceInfoService.deleteResourceById(resourceId);
		return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/download/{resourceName:.+}")
	public ResponseEntity<Resource> downloadResource(@PathVariable String resourceName, HttpServletRequest request, @RequestParam("userId") Long userId) {
		log.info("Resource API: download resource from context ", ServletUriComponentsBuilder.fromCurrentContextPath().toString(), resourceName);
		userId = ExtractAuthUser.resolveHasAdminAccessOrIsOwner(userId);
		// Load file as Resource
		Resource resource = resourceInfoService.loadResourceByResourceNameAndOwnerId(resourceName, userId);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			log.info("Could not determine file type.");
		}
		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

}