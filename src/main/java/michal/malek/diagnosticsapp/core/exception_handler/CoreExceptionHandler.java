package michal.malek.diagnosticsapp.core.exception_handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import michal.malek.diagnosticsapp.core.models.AppResponse;
import michal.malek.diagnosticsapp.core.models.ResponseType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class CoreExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public String handleExpiredJwtException(ExpiredJwtException e, RedirectAttributes redirectAttributes) {
        System.out.println("ExpiredJwtException" + e.getMessage());
        redirectAttributes.addFlashAttribute("message", new AppResponse("Token is expired", ResponseType.ERROR));
        return "redirect:/login";
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public String handleUnsupportedJwtException(UnsupportedJwtException e, RedirectAttributes redirectAttributes) {
        System.out.println("unsupportedJwtException" + e.getMessage());
        redirectAttributes.addFlashAttribute("message", new AppResponse("Unsupported JWT token", ResponseType.ERROR));
        return "redirect:/login";
    }

    @ExceptionHandler(MalformedJwtException.class)
    public String handleMalformedJwtException(MalformedJwtException e, RedirectAttributes redirectAttributes) {
        System.out.println("MalformedJwtException" + e.getMessage());
        redirectAttributes.addFlashAttribute("message", new AppResponse("Malformed JWT token", ResponseType.ERROR));
        return "redirect:/login";
    }

    @ExceptionHandler(SignatureException.class)
    public String handleSignatureException(SignatureException e, RedirectAttributes redirectAttributes) {
        System.out.println("SignatureException" + e.getMessage());
        redirectAttributes.addFlashAttribute("message", new AppResponse("Invalid JWT signature", ResponseType.ERROR));
        return "redirect:/login";
    }

    @ExceptionHandler(DecodingException.class)
    public String handleDecodingException(DecodingException e, RedirectAttributes redirectAttributes) {
        System.out.println("DecodingException" + e.getMessage() );
        redirectAttributes.addFlashAttribute("message", new AppResponse("Unable to decode JWT token", ResponseType.ERROR));
        return "redirect:/login";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException e, RedirectAttributes redirectAttributes) {
        System.out.println("RuntimeException" + e.getMessage());
        redirectAttributes.addFlashAttribute("message", new AppResponse("Runtime Exception", ResponseType.ERROR));
        return "redirect:/home";
    }


}
