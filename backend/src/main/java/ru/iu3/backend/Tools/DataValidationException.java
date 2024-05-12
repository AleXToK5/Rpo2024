package ru.iu3.backend.Tools;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


// Здесь вскрылась ещё одна беда:
// при получении ошибки не выводится её название, а пишется стандартное сообщение
// хотя на стороне бэкенда класс написан корректно и путём отладки было установлено, что в 500 ошибку
// записывается данное сообщение. Но на стороне фронтенда мы её не получаем. При чём выполняется только последнее условие
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class DataValidationException extends Exception  {

    public DataValidationException(String message){
        super(message);
    }
}