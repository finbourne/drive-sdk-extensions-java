package com.finbourne.drive.extensions;

import com.finbourne.drive.ApiException;
import com.finbourne.drive.api.FilesApi;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.CheckedFunction0;

import java.io.File;
import java.time.Duration;
import java.util.function.Predicate;

import static java.time.temporal.ChronoUnit.SECONDS;

public class WaitForVirusScan {
    FilesApi filesApi;
    RetryConfig retryConfig;
    RetryRegistry retryRegistry;
    Retry retry;

    public WaitForVirusScan(FilesApi filesApi, Integer retryAttempts, Integer retrySeconds){
        this.filesApi = filesApi;
        Predicate<Throwable> retryPredicate = e -> (e instanceof ApiException) && (((ApiException) e).getCode() == 423);
        retryConfig = RetryConfig.custom().maxAttempts(retryAttempts != null ? retryAttempts : 20).waitDuration(Duration.of(retrySeconds != null ? retrySeconds : 15, SECONDS)).retryOnException(retryPredicate).build();
        retryRegistry = RetryRegistry.of(retryConfig);
        retry = retryRegistry.retry("VirusScanRetry", retryConfig);
    }

    public File DownloadFileWithRetry(String fileId) throws Throwable {
        CheckedFunction0<File> retryingFileDownload = Retry.decorateCheckedSupplier(retry, () -> filesApi.downloadFile(fileId));
        return retryingFileDownload.apply();
    }
}
