package com.finbourne.drive.extensions;

import com.finbourne.drive.ApiException;
import com.finbourne.drive.api.FoldersApi;
import com.finbourne.drive.model.PagedResourceListOfStorageObject;
import com.finbourne.drive.extensions.auth.LusidTokenException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

public class ApiFactoryBuilderTests {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void build_WithExistingConfigurationFile_ShouldReturnFactory() throws ApiException, ApiConfigurationException, LusidTokenException {
        ApiFactory apiFactory = LusidApiFactoryBuilder.build(CredentialsSource.credentialsFile);
        assertThat(apiFactory, is(notNullValue()));
        assertThatFactoryBuiltApiCanMakeLUSIDCalls(apiFactory);
    }

    private static void assertThatFactoryBuiltApiCanMakeLUSIDCalls(ApiFactory apiFactory) throws ApiException {
        FoldersApi foldersApi = apiFactory.build(FoldersApi.class);
        PagedResourceListOfStorageObject rootFolder = foldersApi.getRootFolder(null, null, null, null, null);
        assertThat("Folders API created by factory should return root folder"
                , rootFolder, is(notNullValue()));
        assertThat("Root folder contents types returned by the folders API should not be empty",
                rootFolder.getValues(), not(empty()));
    }

}
