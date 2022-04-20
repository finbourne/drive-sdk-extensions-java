package com.finbourne.drive.extensions;

import com.finbourne.drive.ApiException;
import com.finbourne.drive.api.FilesApi;
import com.finbourne.drive.api.FoldersApi;
import com.finbourne.drive.model.CreateFolder;
import com.finbourne.drive.model.StorageObject;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

public class SampleWorkflowTests{
    private FilesApi _filesApi;
    private FoldersApi _foldersApi;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        // use the factory to create api clients
        ApiFactory apiFactory = LusidApiFactoryBuilder.build(CredentialsSource.credentialsFile);
        _foldersApi = apiFactory.build(FoldersApi.class);
        _filesApi = apiFactory.build(FilesApi.class);

        // create or get folder in the root directory
        CreateFolder createFolder = new CreateFolder();
        createFolder.setName("JavaTestFolder");
        createFolder.setPath("/");
        List<StorageObject> testFolder = _foldersApi.getRootFolder(null, null, null, null, "Name eq 'JavaTestFolder'").getValues();
        if (testFolder.isEmpty()){
            _foldersApi.createFolder(createFolder);
        }
    }

    @Test
    public void assertThatFilesAndFoldersCanBeCreated() throws Throwable {
        // upload a file
        byte[] data = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("sampleFile.txt"));
        StorageObject sampleFile = _filesApi.createFile("sampleFile.txt", "/JavaTestFolder/", data.length, data);

        // download file from drive - as file has just been uploaded, we should wait for virus scan
        WaitForVirusScan waitForVirusScan = new WaitForVirusScan(_filesApi);
        File sampleFileDownload = waitForVirusScan.DownloadFileWithRetry(sampleFile.getId());
        byte[] downloadData = Files.readAllBytes(Paths.get(sampleFileDownload.getAbsolutePath()));

        Assert.assertEquals(Base64.getEncoder().encodeToString(downloadData), Base64.getEncoder().encodeToString(data));

        // delete file
        _filesApi.deleteFile(sampleFile.getId());
        Assert.assertThrows(ApiException.class, () -> _filesApi.getFile(sampleFile.getId()));
    }
}
