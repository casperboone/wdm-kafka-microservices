package nl.tudelft.wdm.group1.rest.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/")
public class IndexController {
    /**
     * Used for health check
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity index() {
        return ResponseEntity.ok().build();
    }
}
