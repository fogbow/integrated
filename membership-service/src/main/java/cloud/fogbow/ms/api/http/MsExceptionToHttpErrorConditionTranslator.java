package cloud.fogbow.ms.api.http;

import org.springframework.web.bind.annotation.ControllerAdvice;
import cloud.fogbow.common.http.FogbowExceptionToHttpErrorConditionTranslator;

@ControllerAdvice
public class MsExceptionToHttpErrorConditionTranslator extends FogbowExceptionToHttpErrorConditionTranslator {

}
