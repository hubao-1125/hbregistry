package io.github.hubao.hbregistry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/*
 * Desc:
 *
 * @author hubao
 * @see 2024/4/26 22:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {

    private HttpStatus httpStatus;
    private String message;

}
