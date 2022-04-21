package com.finbourne.drive.extensions;

import com.finbourne.drive.ApiClient;
import com.finbourne.drive.api.FilesApi;
import com.finbourne.drive.api.FoldersApi;
import com.finbourne.drive.api.SearchApi;
import com.finbourne.drive.model.StorageObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class ApiFactoryTest {

    private ApiFactory apiFactory;
    private ApiClient apiClient;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp(){
        apiClient = mock(ApiClient.class);
        apiFactory = new ApiFactory(apiClient);
    }

    // General Cases

    @Test
    public void build_ForFilesApi_ReturnPortfolioApi(){
        FilesApi filesApi = apiFactory.build(FilesApi.class);
        assertThat(filesApi, instanceOf(FilesApi.class));
    }

    @Test
    public void build_ForFoldersApi_ReturnTransactionPortfolioApi(){
        FoldersApi foldersApi = apiFactory.build(FoldersApi.class);
        assertThat(foldersApi, instanceOf(FoldersApi.class));
    }

    @Test
    public void build_ForSearchApi_ReturnAggregationApi(){
        SearchApi searchApi = apiFactory.build(SearchApi.class);
        assertThat(searchApi, instanceOf(SearchApi.class));
    }

    @Test
    public void build_ForAnyApi_SetsTheApiFactoryClientAndNotTheDefault(){
        FilesApi filesApi = apiFactory.build(FilesApi.class);
        assertThat(filesApi.getApiClient(), equalTo(apiClient));
    }

    // Singleton Check Cases

    @Test
    public void build_ForSameApiBuiltAgainWithSameFactory_ReturnTheSameSingletonInstanceOfApi(){
        FilesApi filesApi = apiFactory.build(FilesApi.class);
        FilesApi filesApiSecond = apiFactory.build(FilesApi.class);
        assertThat(filesApi, sameInstance(filesApiSecond));
    }

    @Test
    public void build_ForSameApiBuiltWithDifferentFactories_ReturnAUniqueInstanceOfApi(){
        FilesApi filesApi = apiFactory.build(FilesApi.class);
        FilesApi filesApiSecond = new ApiFactory(mock(ApiClient.class)).build(FilesApi.class);
        assertThat(filesApi, not(sameInstance(filesApiSecond)));
    }

    // Error Cases

    @Test
    public void build_ForNonApiPackageClass_ShouldThrowException(){
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("com.finbourne.drive.model.StorageObject class is not a supported API class. " +
                "Supported API classes live in the " + ApiFactory.API_PACKAGE + " package.");
        apiFactory.build(StorageObject.class);
    }



}
