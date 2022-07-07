package ru.omel.po.views.support;

import com.vaadin.flow.component.upload.UploadI18N;

import java.util.Arrays;

public class UploadFilesI18N extends UploadI18N {
    public UploadFilesI18N(){
        setDropFiles(new DropFiles()
                .setOne("Перетащите файл сюда")
                .setMany("Перетащите файл сюда"));
        setAddFiles(new AddFiles()
                .setOne("Загрузить файл...")
                .setMany("Загрузить файлы..."));
        setCancel("Отмена");
        setError(new Error()
                .setTooManyFiles("Слишком много файлов.")
                .setFileIsTooBig("Файл слишком большой.")
                .setIncorrectFileType("Неверный тип файла."));
        setUploading(new Uploading()
                .setStatus(new Uploading.Status()
                        .setConnecting("Соединение...")
                        .setStalled("Приостановлено")
                        .setProcessing("Обработка файла...")
                        .setHeld("Ожидает в очереди"))
                .setRemainingTime(new Uploading.RemainingTime()
                        .setPrefix("ожидаемое время загрузки: ")
                        .setUnknown("неизвестно ожидаемое время загрузки"))
                .setError(new Uploading.Error()
                        .setServerUnavailable("Загрузка прервалась, пожалуйста попробуйте снова")
                        .setUnexpectedServerError("Загрузка не удалась из-за ошибки сервера")
                        .setForbidden("Загрузка запрещена")));
        setUnits(new Units()
                .setSize(Arrays.asList("Б", "кБ", "МБ")));
    }
}
