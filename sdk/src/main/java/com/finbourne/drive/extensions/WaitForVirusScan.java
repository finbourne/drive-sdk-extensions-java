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
import java.util.function.Supplier;

import static java.time.temporal.ChronoUnit.SECONDS;

public class WaitForVirusScan {
    FilesApi _filesApi;
    RetryConfig _retryConfig;
    RetryRegistry _retryRegistry;
    Retry _retry;

    public WaitForVirusScan(FilesApi filesApi){
        _filesApi = filesApi;
        Predicate<Throwable> retryPredicate = e -> (e instanceof ApiException) && (((ApiException) e).getCode() == 423);
        _retryConfig = RetryConfig.custom().maxAttempts(20).waitDuration(Duration.of(15, SECONDS)).retryOnException(retryPredicate).build();
        _retryRegistry = RetryRegistry.of(_retryConfig);
        _retry = _retryRegistry.retry("VirusScanRetry", _retryConfig);
    }

    public File DownloadFileWithRetry(String fileId) throws Throwable {
        CheckedFunction0<File> retryingFileDownload = Retry.decorateCheckedSupplier(_retry, () -> _filesApi.downloadFile(fileId));
        return retryingFileDownload.apply();
    }
}
