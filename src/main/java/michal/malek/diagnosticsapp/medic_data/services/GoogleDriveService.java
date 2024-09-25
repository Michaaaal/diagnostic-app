package michal.malek.diagnosticsapp.medic_data.services;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.client.http.FileContent;
import michal.malek.diagnosticsapp.medic_data.utills.GoogleDriveAuthorizeUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
public class GoogleDriveService {
    private final Drive driveService;

    public GoogleDriveService() throws GeneralSecurityException, IOException {
        this.driveService = GoogleDriveAuthorizeUtil.getDriveService();
    }

    public void deleteFile(String fileDriveId) throws IOException {
        driveService.files().delete(fileDriveId).execute();
    }

    public List<File> listFiles() throws IOException {
        FileList result = driveService.files().list()
                .setPageSize(10)
                .setFields("nextPageToken, files(id, name)")
                .execute();
        return result.getFiles();
    }

    public File multipartToDriveFile(MultipartFile file, String fileName, String contentType) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }

        String newFilename = fileName +  fileExtension;

        java.io.File tempFile = java.io.File.createTempFile("temp", fileExtension);
        file.transferTo(tempFile);

        File fileMetadata = new File();
        fileMetadata.setName(newFilename);

        FileContent mediaContent = new FileContent(contentType, tempFile);

        return driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id, name")
                    .execute();

    }

    public java.io.File getFileByID(String fileDriveId) throws IOException {

        File fileMetadata = driveService.files().get(fileDriveId).execute();

        java.io.File tempFile = new java.io.File(System.getProperty("java.io.tmpdir"), fileMetadata.getName());

        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            driveService.files().get(fileDriveId)
                    .executeMediaAndDownloadTo(outputStream);
        }

        return tempFile;
    }

}
