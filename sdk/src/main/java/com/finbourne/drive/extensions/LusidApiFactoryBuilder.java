package com.finbourne.drive.extensions;

import com.finbourne.drive.ApiClient;
import com.finbourne.drive.extensions.auth.LusidTokenException;

public class LusidApiFactoryBuilder {

    /**
     * Build a {@link ApiFactory} defining configuration using environment variables. For details on the environment arguments see https://support.lusid.com/getting-started-with-apis-sdks.
     *
     * @return
     */
    public static ApiFactory build() throws ApiConfigurationException, LusidTokenException {
        if (!areRequiredEnvironmentVariablesSet()) {
            throw new ApiConfigurationException("Environment variables to configure LUSID API client have not been set. See " +
                    " see https://support.lusid.com/getting-started-with-apis-sdks for details.");
        }
        return createLusidApiFactory("");
    }

    /**
     * Build a {@link ApiFactory} using the specified configuration file. For details on the format of the configuration file see https://support.lusid.com/getting-started-with-apis-sdks.
     */
    public static ApiFactory build(String configurationFile) throws ApiConfigurationException, LusidTokenException {
        return createLusidApiFactory(configurationFile);
    }

    private static ApiFactory createLusidApiFactory(String configurationFile) throws ApiConfigurationException, LusidTokenException {
        ApiConfiguration apiConfiguration = new ApiConfigurationBuilder().build(configurationFile);
        ApiClient apiClient = new ApiClientBuilder().build(apiConfiguration);
        return new ApiFactory(apiClient);
    }

    private static boolean areRequiredEnvironmentVariablesSet(){
        return (System.getenv("FBN_TOKEN_URL") != null &&
                System.getenv("FBN_USERNAME") != null &&
                System.getenv("FBN_PASSWORD") != null &&
                System.getenv("FBN_CLIENT_ID") != null &&
                System.getenv("FBN_CLIENT_SECRET") != null &&
                System.getenv("FBN_LUSID_API_URL") != null);
    }
}
